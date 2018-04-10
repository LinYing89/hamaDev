package com.bairock.hamadev.communication;

import com.bairock.hamadev.app.HamaApp;
import com.bairock.hamadev.app.MainActivity;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
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
 * Created by 44489 on 2017/11/8.
 */

public class DownloadClient {

    public void link(){

        try {
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(10, 8,15, TimeUnit.SECONDS)); // 1
                    ch.pipeline().addLast(new DownloadClientHandler());
                }
            });

            // Start the client.
            ChannelFuture channelFuture = b.connect(HamaApp.SERVER_IP, 4046).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(!future.isSuccess()){
                        if(null != MainActivity.handler){
                            MainActivity.handler.obtainMessage(MainActivity.DOWNLOAD_FAIL).sendToTarget();
                        }
                    }
                }
            });
            // Wait until the connection is closed.
            channelFuture.channel().closeFuture();
        }catch (Exception e){
            e.printStackTrace();
            if(null != MainActivity.handler){
                MainActivity.handler.obtainMessage(MainActivity.DOWNLOAD_FAIL).sendToTarget();
            }
        }
    }
}
