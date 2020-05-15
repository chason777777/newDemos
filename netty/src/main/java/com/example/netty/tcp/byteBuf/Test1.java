package com.example.netty.tcp.byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/19
 */
public class Test1 {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("abcdefg hijklmn", CharsetUtil.UTF_8);
        System.out.println((char)byteBuf.getByte(0));
        System.out.println(byteBuf.readerIndex() + ":" + byteBuf.writerIndex());
        System.out.println((char)byteBuf.readByte());
        System.out.println(byteBuf.readerIndex() + ":" + byteBuf.writerIndex());
        System.out.println(byteBuf.readableBytes());
    }
}
