package me.ely.nettyinaction.echodemo

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.util.CharsetUtil
import java.net.InetSocketAddress

/**
 *
 *
 * @author  <a href="mailto:xiaochunyong@gmail.com">Ely</a>
 * @see
 * @since   2019/8/29
 */
object EchoServer {

    @JvmStatic
    fun main(args: Array<String>) {
        val port = 9000
        val echoServerChannelHandler = EchoServerChannelHandler()
        val group = NioEventLoopGroup()
        try {
            val b = ServerBootstrap()
            b.group(group)
                    .channel(NioServerSocketChannel::class.java)
                    .localAddress(InetSocketAddress(port))
                    .childHandler(object: ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().addLast(echoServerChannelHandler)
                        }
                    })
            val f = b.bind().sync()
            f.channel().closeFuture().sync()
        } finally {
            group.shutdownGracefully().sync()
        }
    }
}

@ChannelHandler.Sharable
class EchoServerChannelHandler : ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val buf = msg as ByteBuf
        println("server receivied: ${buf.toString(CharsetUtil.UTF_8)}")
        ctx.write(buf)
    }

    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        println("read complete")
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }

}