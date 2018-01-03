/*
package com.cwlrdc.front.common;

import com.unlto.twls.commonutil.msgnotify.AbstractMsgReceiver;
import com.unlto.twls.commonutil.msgnotify.MessageNotifyException;
import com.unlto.twls.commonutil.msgnotify.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MessageReceiver extends AbstractMsgReceiver {

    @Autowired
    public MessageReceiver(@Qualifier(value = "msgTemplate") MessageTemplate template) throws MessageNotifyException {
        super(template);
    }

    @Override
    public String[] getRegisters() {
        return new String[]{"xkj"};
    }

    @Override
    public void reload(String key) {
        log.debug("收到通知，开始处理明细文件...");
        try {
            if ("xkj".equalsIgnoreCase(key)) {
                log.debug("明细文件处理完成....");
            }
        } catch (Exception e) {
            log.warn("zk通知处理错误,key[" + key + "]", e);
        }
    }
}
*/
