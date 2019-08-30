package me.ely.nettyinaction.echodemo

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.CharsetUtil
import java.net.InetSocketAddress

/**
 *
 *
 * @author  <a href="mailto:xiaochunyong@gmail.com">Ely</a>
 * @see
 * @since   2019/8/29
 */
object EchoClient {



    @JvmStatic
    fun main(args: Array<String>) {
        val host = "localhost"
        val port = 9000

        val group = NioEventLoopGroup()
        try {
            val b = Bootstrap()
            b.group(group)
                    .channel(NioSocketChannel::class.java)
                    .remoteAddress(InetSocketAddress(host, port))
                    .handler(object: ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            ch.pipeline().addLast(EchoClientHandler())
                        }
                    })
            val f = b.connect().sync()
            f.channel().closeFuture().sync()
        } finally {
            group.shutdownGracefully().sync()
        }
    }

}

class EchoClientHandler : SimpleChannelInboundHandler<ByteBuf>() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8))
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf) {
        println("client received: ${msg.toString(CharsetUtil.UTF_8)}")
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }

}