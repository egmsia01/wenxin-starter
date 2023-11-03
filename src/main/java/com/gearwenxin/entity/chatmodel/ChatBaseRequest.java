package com.gearwenxin.entity.chatmodel;

import com.gearwenxin.common.ErrorCode;
import com.gearwenxin.exception.WenXinException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import static com.gearwenxin.common.Constant.MAX_CONTENT_LENGTH;

/**
 * @author Ge Mingjia
 * @date 2023/8/3
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatBaseRequest implements Serializable {

    /**
     * 表示最终用户的唯一标识符，可以监视和检测滥用行为，防止接口恶意调用
     */
    private String userId;

    /**
     * 聊天信息,不能为空
     */
    private String content;

    public static void validSelf(ChatBaseRequest chatBaseRequest) {

        // 检查content不为空
        if (StringUtils.isBlank(chatBaseRequest.getContent())) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content cannot be empty");
        }
        // 检查单个content长度
        if (chatBaseRequest.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new WenXinException(ErrorCode.PARAMS_ERROR, "content's length cannot be more than 2000");
        }
    }

}
