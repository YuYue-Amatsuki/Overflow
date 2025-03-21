package top.mrxiaom.overflow.internal.listener

import cn.evolvefield.onebot.client.handler.EventBus.listen
import cn.evolvefield.onebot.sdk.entity.GuildSender
import cn.evolvefield.onebot.sdk.event.message.GuildMessageEvent
import net.mamoe.mirai.contact.MemberPermission
import top.mrxiaom.overflow.event.LegacyGuildMessageEvent
import top.mrxiaom.overflow.internal.Overflow
import top.mrxiaom.overflow.internal.message.OnebotMessages.toMiraiMessage

internal fun addGuildListeners() {
    listen<GuildMessageEvent>("channel") { e ->
        // 频道消息通知
        val message = e.toMiraiMessage(bot)
        Overflow.instance.resolveResourceDownload(message)
        val messageString = message.toString()

        if (e.sender.userId == bot.id) {
            // TODO: 过滤自己发送的消息
        } else {
            bot.logger.verbose("[频道][${e.guildId}(${e.channelId})] ${e.sender.nameCardOrNick}(${e.sender.userId}) -> $messageString")
            bot.eventDispatcher.broadcastAsync(LegacyGuildMessageEvent(
                bot = bot,
                guildId = e.guildId,
                channelId = e.channelId,
                messageId = e.messageId,
                message = message,
                senderId = e.sender.userId,
                senderTinyId = e.sender.tinyId,
                senderNick = e.sender.nickname,
                senderNameCard = e.sender.card,
                senderTitle = e.sender.title,
                senderLevel = e.sender.level,
                senderRole = when (e.sender.role.lowercase()) {
                    "owner" -> MemberPermission.OWNER
                    "admin" -> MemberPermission.ADMINISTRATOR
                    else -> MemberPermission.MEMBER
                },
                time = e.timeInSecond().toInt()
            ))
        }
    }
}

private val GuildSender.nameCardOrNick: String
    get() = card.takeIf { it.isNotBlank() } ?: nickname
