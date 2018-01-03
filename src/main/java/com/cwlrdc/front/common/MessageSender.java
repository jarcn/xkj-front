/*
package com.cwlrdc.front.common;

import com.unlto.twls.commonutil.msgnotify.AbstractMsgSender;
import com.unlto.twls.commonutil.msgnotify.MessageNotifyException;
import com.unlto.twls.commonutil.msgnotify.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageSender extends AbstractMsgSender {

	public MessageSender(MessageTemplate template)throws MessageNotifyException {
		super(template);
	}

	@Override
	public void reload2Key(String key) {
		try {
			super.changeKey(key);
		} catch (MessageNotifyException e) {
			log.info("刷新key["+key+"]失败",e);
		}
	}

	@Override
	public String[] getRegisters() {
		return new String[] {"xkj"};
	}
}
*/
