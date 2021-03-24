package com.edward.elric.server.time;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author impactCn
 * @createTime 2021-03-24
 */
public class MultiplexerTimeServer implements Runnable{

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    private volatile boolean stop;

    public MultiplexerTimeServer(int port) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            System.out.println("The time server is start in port :" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {


        while (!stop) {
            try {
                selector.select(1000);

                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();

                SelectionKey key = null;
                while (iterator.hasNext()) {
                    key = iterator.next();

                    iterator.remove();

                    handleInput(key);

                    key.cancel();

                    if (key.channel() != null) {
                        key.channel().close();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            if (selector != null) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 处理输入的流
     * @param key
     */
    private void handleInput(SelectionKey key) throws IOException {

        if (key.isValid()) {

            // 确认连接
            if (key.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

                SocketChannel socketChannel = serverSocketChannel.accept();

                socketChannel.configureBlocking(false);

                socketChannel.register(selector, SelectionKey.OP_READ);
            }

            // 是否在读
            if (key.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) key.channel();

                ByteBuffer readBuffer = ByteBuffer.allocate(1024);

                int readBytes = socketChannel.read(readBuffer);

                if (readBytes > 0) {
                    readBuffer.flip();

                    byte[] bytes = new byte[readBuffer.remaining()];

                    readBuffer.get(bytes);

                    String body = new String(bytes, StandardCharsets.UTF_8);

                    System.out.println("The time server receive order : " + body);

                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body)
                            ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";

                    doWrite(socketChannel, currentTime);
                } else if (readBytes < 0) {
                    key.cancel();
                    socketChannel.close();
                }

            }
        }
    }

    private void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            byte[] bytes = response.getBytes();

            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);

            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }
}
