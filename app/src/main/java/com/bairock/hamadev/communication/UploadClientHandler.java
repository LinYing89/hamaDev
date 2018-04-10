package com.bairock.hamadev.communication;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;
import com.bairock.hamadev.database.DevGroupDao;
import com.bairock.hamadev.database.DeviceDao;
import com.bairock.hamadev.database.LinkageDao;
import com.bairock.hamadev.database.LinkageHolderDao;
import com.bairock.hamadev.database.SdDbHelper;
import com.bairock.hamadev.database.UserDao;
import com.bairock.iot.intelDev.device.Device;
import com.bairock.iot.intelDev.linkage.Linkage;
import com.bairock.iot.intelDev.linkage.LinkageHolder;
import com.bairock.iot.intelDev.user.DevGroup;
import com.bairock.iot.intelDev.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 上传客户端连接处理程序
 * Created by 44489 on 2017/11/6.
 */

public class UploadClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channel.writeAndFlush(Unpooled.copiedBuffer("upload".getBytes("GBK")));
        TimeUnit.MILLISECONDS.sleep(500);
        String userJson = getUserJson();
        if(null != userJson){
            userJson = userJson + "#";
            ctx.writeAndFlush(Unpooled.copiedBuffer(userJson.getBytes("GBK")));
        }
        //ctx.close();
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf m = (ByteBuf)msg;
        try {
            byte[] req = new byte[m.readableBytes()];
            m.readBytes(req);
            String str = new String(req, "GBK");
            if(str.equals("OK")){
                //refreshDbUser(user);
                ctx.close();
                if(null != MainActivity.handler){
                    MainActivity.handler.obtainMessage(MainActivity.UPLOAD_OK).sendToTarget();
                }
            }else{
                ctx.close();
                if(null != MainActivity.handler){
                    MainActivity.handler.obtainMessage(MainActivity.UPLOAD_FAIL).sendToTarget();
                }
            }
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
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
            MainActivity.handler.obtainMessage(MainActivity.UPLOAD_FAIL).sendToTarget();
            ctx.close();
        }
    }

    private String getUserJson(){
        String json = null;
        User user = SdDbHelper.getDbUser();
        if(null != user){
            ObjectMapper mapper = new ObjectMapper();
            try {
                json = mapper.writeValueAsString(user);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return json;
    }
}
