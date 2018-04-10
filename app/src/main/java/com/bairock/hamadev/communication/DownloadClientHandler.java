package com.bairock.hamadev.communication;

import com.bairock.hamadev.app.ClimateFragment;
import com.bairock.hamadev.app.ElectricalCtrlFragment;
import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.app.WelcomeActivity;
import com.bairock.hamadev.database.SdDbHelper;
import com.bairock.iot.intelDev.communication.DevChannelBridgeHelper;
import com.bairock.iot.intelDev.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 *
 * Created by 44489 on 2017/11/8.
 */

public class DownloadClientHandler extends ChannelInboundHandlerAdapter {

    private StringBuilder stringBuilder;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        stringBuilder = new StringBuilder();
        String str = "download:" + HamaApp.USER.getName() + ":" + HamaApp.DEV_GROUP.getName();
        channel.writeAndFlush(Unpooled.copiedBuffer(str.getBytes("GBK")));
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf)msg;
        try {
            byte[] req = new byte[m.readableBytes()];
            m.readBytes(req);
            //String str = new String(req, "GBK");
            stringBuilder.append(new String(req, "GBK"));
            String strMsg = stringBuilder.toString();
            if(strMsg.endsWith("OK")){
                //refreshDbUser(user);
                strMsg = strMsg.substring(0, strMsg.length() - 2);
                //stringBuilder.append(str);
                ctx.close();
                User user = getUserFromJson(strMsg);
                SdDbHelper.replaceDbUser(user);
                DevChannelBridgeHelper.getIns().stopSeekDeviceOnLineThread();
                WelcomeActivity.initUser();

                if(null != ElectricalCtrlFragment.handler){
                    ElectricalCtrlFragment.handler.obtainMessage(ElectricalCtrlFragment.REFRESH_ELE).sendToTarget();
                }
                if(null != ClimateFragment.handler){
                    ClimateFragment.handler.obtainMessage(ClimateFragment.REFRESH_DEVICE).sendToTarget();
                }
                if(null != MainActivity.handler){
                    MainActivity.handler.obtainMessage(MainActivity.DOWNLOAD_OK).sendToTarget();
                }
            }

        }catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
            if(null != MainActivity.handler){
                MainActivity.handler.obtainMessage(MainActivity.DOWNLOAD_FAIL).sendToTarget();
            }
        }finally{
            m.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {  // 2
            //IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state() == IdleState.WRITER_IDLE) {
//                MainActivity.handler.obtainMessage(MainActivity.UPLOAD_FAIL).sendToTarget();
//            }
            MainActivity.handler.obtainMessage(MainActivity.DOWNLOAD_FAIL).sendToTarget();
            ctx.close();
        }
    }

    private User getUserFromJson(String json){
        ObjectMapper mapper = new ObjectMapper();
        User user = null;
        try {
            user = mapper.readValue(json, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

}
