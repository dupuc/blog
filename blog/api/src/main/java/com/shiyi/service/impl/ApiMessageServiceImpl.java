package com.shiyi.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiyi.common.ResponseResult;
import com.shiyi.entity.Message;
import com.shiyi.mapper.MessageMapper;
import com.shiyi.service.ApiMessageService;
import com.shiyi.utils.IpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiMessageServiceImpl implements ApiMessageService {

    private final MessageMapper messageMapper;

    private final HttpServletRequest request;

    /**
     * 留言列表
     * @return
     */
    @Override
    public ResponseResult selectMessageList() {
        // 查询留言列表
        List<Message> messageList = messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .select(Message::getId, Message::getNickname, Message::getAvatar,
                        Message::getContent, Message::getTime));
        return ResponseResult.success(messageList);
    }

    /**
     * 添加留言
     * @param message
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult insertMessage(Message message) {
        // 获取用户ip
        String ipAddress = IpUtil.getIp(request);
        String ipSource = IpUtil.getIp2region(ipAddress);
        message.setIpAddress(ipAddress);
        message.setIpSource(ipSource);
        messageMapper.insert(message);
        return ResponseResult.success("留言成功");
    }
}
