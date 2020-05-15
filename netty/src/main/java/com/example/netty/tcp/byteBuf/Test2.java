package com.example.netty.tcp.byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * @author lichao
 * @version 1.0
 * @Description
 * @date 2019/7/19
 */
public class Test2 {
    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer("aaaabbbb", CharsetUtil.UTF_8);
        System.out.println(ByteBufUtil.getBytes(byteBuf));
    }
}
