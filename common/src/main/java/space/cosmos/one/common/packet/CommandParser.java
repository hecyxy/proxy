package space.cosmos.one.common.packet;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.cosmos.one.common.packet.message.Command;
import space.cosmos.one.common.packet.message.CommandMessage;
import space.cosmos.one.common.util.BufferUtils;

public class CommandParser extends MysqlParser<CommandMessage> {
    private static final Logger logger = LoggerFactory.getLogger(CommandParser.class);

    public CommandParser(ByteBuf packet) {
        super(packet);
    }

    @Override
    public CommandMessage decodeBody0() {
        Byte b = packet.readByte();
        logger.info("byte {}", b);
        String cmdType = "";
        String cmdName = "";
        Command userCmd = Command.valueOf((int) b);
        if (userCmd != null) {
            cmdType = userCmd.name();
            logger.info("send command: {}", userCmd.name());
            switch (userCmd) {
                case INIT_DB:
                    cmdName = BufferUtils.readRestPacket(packet);
                    logger.info("use db: {}", cmdName);
                    break;
                case QUERY:
                    cmdName = BufferUtils.readRestPacket(packet);
                    logger.info("use execute sql {}", cmdName);
                    break;
                case CREATE_DB:
                    cmdName = BufferUtils.readRestPacket(packet);
                    logger.info("use execute sql {}", cmdName);
                    break;
                case DROP_DB:
                    cmdName = BufferUtils.readRestPacket(packet);
                    logger.info("use execute sql {}", cmdName);
                    break;
                default:
                    cmdName = BufferUtils.readRestPacket(packet);
                    logger.info("other command,{}", b);
            }
        }
        return new CommandMessage(cmdType, cmdName);
    }
}
