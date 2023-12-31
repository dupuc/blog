package com.shiyi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiyi.common.FieldConstants;
import com.shiyi.common.ResponseResult;
import com.shiyi.entity.FriendLink;
import com.shiyi.exception.BusinessException;
import com.shiyi.mapper.FriendLinkMapper;
import com.shiyi.service.ApiFriendLinkService;
import com.shiyi.service.EmailService;
import com.shiyi.vo.ApiFriendLinkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.shiyi.enums.FriendLinkEnum.APPLY;
import static com.shiyi.enums.FriendLinkEnum.UP;

@Service
@RequiredArgsConstructor
public class ApiFriendLinkServiceImpl implements ApiFriendLinkService {

    private final FriendLinkMapper friendLinkMapper;

    private final EmailService emailService;

    /**
     * 友链列表
     * @return
     */
    @Override
    public ResponseResult selectFriendLinkList() {
        List<ApiFriendLinkVO> list = friendLinkMapper.selectLinkList(UP.code);
        return ResponseResult.success(list);
    }

    /**
     * 申请友链
     * @param friendLink
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult insertFriendLink(FriendLink friendLink) {

        if (friendLink.getUrl().contains("shiyit.com") ||
                friendLink.getUrl().contains("baidu.com")){
            throw new BusinessException("不合法的网址!");
        }

        //如果已经申请过友链 再次接入则会进行下架处理 需重新审核
        FriendLink entity = friendLinkMapper.selectOne(new QueryWrapper<FriendLink>()
                .eq(FieldConstants.URL,friendLink.getUrl()));

        friendLink.setStatus(APPLY.getCode());
        if (entity != null) {
            friendLink.setId(entity.getId());
            friendLinkMapper.updateById(friendLink);
        }else {
            friendLinkMapper.insert(friendLink);
        }
        //异步操作邮箱发送
        emailService.emailNoticeMe("友链接入通知","网站有新的友链接入啦("+friendLink.getUrl()+")，快去审核吧!!!");
        return ResponseResult.success();
    }
}
