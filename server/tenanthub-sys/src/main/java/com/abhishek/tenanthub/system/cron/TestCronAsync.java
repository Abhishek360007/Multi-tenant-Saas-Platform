package com.abhishek.tenanthub.system.cron;

import cn.hutool.core.util.RandomUtil;
import com.abhishek.tenanthub.auth.service.JwtTokenService;
import com.abhishek.tenanthub.common.domain.SimpPushMessage;
import com.abhishek.tenanthub.common.util.IdUtils;
import com.abhishek.tenanthub.common.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.abhishek.tenanthub.common.consts.SimpMessageConst.BROADCAST_NOTICE;
import static com.abhishek.tenanthub.common.consts.SimpMessageConst.QUEUE_SINGLE_PUSH;

/**
 * Sample notifications for trying out the notification bell during development.
 * Disabled by default; enable with {@code tenanthub.demo-notifications=true}.
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/30 15:19
 */
@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(name = "tenanthub.demo-notifications", havingValue = "true")
public class TestCronAsync {

    private static final List<String> ANNOUNCEMENTS = List.of(
            "Scheduled maintenance tonight from 11:30 PM to 12:30 AM IST.",
            "New: pay for your subscription with UPI, cards or netbanking.",
            "Office closed on the next gazetted holiday. Support replies may be slower.",
            "GST invoices are now available under Billing > Subscription.",
            "Tip: sign in with a one-time code sent to your email or mobile."
    );

    private static final List<String> PERSONAL = List.of(
            "Your report is ready to download.",
            "A new user joined your team.",
            "Your password was changed successfully.",
            "A device in your project went offline.",
            "Your subscription renews in 7 days."
    );

    private final SimpMessagingTemplate messagingTemplate;

    private final JwtTokenService tokenService;

    @Scheduled(fixedRate = 30000)
    public void broadcast() {
        if (tokenService.listLoginIds().isEmpty()) {
            return;
        }
        var msg = new SimpPushMessage()
                .setId(IdUtils.getStandaloneId())
                .setTitle("Announcement")
                .setContent(RandomUtil.randomEle(ANNOUNCEMENTS))
                .setCreateTime(LocalDateTime.now());
        // Broadcast to all users
        messagingTemplate.convertAndSend(BROADCAST_NOTICE, JsonUtils.toJson(msg));
    }

    @Scheduled(fixedRate = 60000)
    public void push() {
        tokenService.mapAllLoginUser().forEach((userId, user) -> {
            var msg = new SimpPushMessage()
                    .setId(IdUtils.getStandaloneId())
                    .setTitle("Message")
                    .setContent(RandomUtil.randomEle(PERSONAL))
                    .setCreateTime(LocalDateTime.now());
            // Push to a specific user: /user/queue/single/push
            messagingTemplate.convertAndSendToUser(user.getLoginId(), QUEUE_SINGLE_PUSH, JsonUtils.toJson(msg));
        });
    }
}
