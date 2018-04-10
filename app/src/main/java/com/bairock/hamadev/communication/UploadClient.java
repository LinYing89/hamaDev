package com.bairock.hamadev.communication;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 *
 * Created by 44489 on 2017/11/6.
 */

public class UploadClient {

    private EventLoopGroup workerGroup;

    private  ChannelFuture channelFuture;

    public void link(){

        try {
            workerGroup = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(10, 8,15, TimeUnit.SECONDS)); // 1
                    ch.pipeline().addLast(new UploadClientHandler());
                }
            });

            // Start the client.
            channelFuture = b.connect(HamaApp.SERVER_IP, 4046).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(!future.isSuccess()){
                        if(null != MainActivity.handler){
                            MainActivity.handler.obtainMessage(MainActivity.UPLOAD_FAIL).sendToTarget();
                        }
                    }
                }
            });

            // Wait until the connection is closed.
            channelFuture.channel().closeFuture();
        }catch (Exception e){
            e.printStackTrace();
            if(null != MainActivity.handler){
                MainActivity.handler.obtainMessage(MainActivity.UPLOAD_FAIL).sendToTarget();
            }
        } finally {
            //workerGroup.shutdownGracefully();
        }
    }

    public void send(String msg){
        if(null != channelFuture) {
            channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(msg.getBytes()));
        }
    }
    public void send(byte[] msg){
        if(null != channelFuture) {
            channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(msg));
        }
    }

    public void myClose(){
        if(null != workerGroup) {
            workerGroup.shutdownGracefully();
        }
    }
}
