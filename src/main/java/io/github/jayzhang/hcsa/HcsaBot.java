// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package io.github.jayzhang.hcsa;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codepoetics.protonpack.collectors.CompletableFutures;
import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.ChannelAccount;

/**
 * This class implements the functionality of the Bot.
 *
 * <p>
 * This is where application specific logic for interacting with the users would be added. For this
 * sample, the {@link #onMessageActivity(TurnContext)} echos the text back to the user. The {@link
 * #onMembersAdded(List, TurnContext)} will send a greeting to new conversation participants.
 * </p>
 */
@Component
public class HcsaBot extends ActivityHandler {

	static Logger log = LoggerFactory.getLogger(HcsaBot.class);
	
	@Autowired
	SMiner sminer;
	
    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
    	
    	String input = turnContext.getActivity().getText();
    	log.info("input:{}", input);
    	StringBuilder sb = new StringBuilder();
    	try
    	{
    		List<SMiningResult> list = sminer.extractTargets(input);
        	if(list == null ||list.size() == 0)
        	{
        		sb.append("对不起，我分析不出来，请换句话试试:(");
        	}
        	else 
        	{
        		int rate = 0;
        		for(SMiningResult result : list)
        		{
        			if(result.rate == Rates.BAD.getValue())
        				-- rate;
        			else if(result.rate == Rates.GOOD.getValue())
        				++ rate;
        			sb.append(result.toString()).append("\n");
        		} 
        		String overall = "neutral";
        		if(rate >0) 
        			overall = "positive";
        		else if(rate <0)
        			overall = "negative";
        		sb.append("\n--------------------overall rate: ").append(overall).append("--------------------");
        	}
    	}
    	catch(Exception e)
    	{
    		sb.append(e.toString());
    	}
    	
        return turnContext.sendActivity(
            MessageFactory.text(sb.toString())
        ).thenApply(sendResult -> null);
    }

    @Override
    protected CompletableFuture<Void> onMembersAdded(
        List<ChannelAccount> membersAdded,
        TurnContext turnContext
    ) {
        return membersAdded.stream()
            .filter(
                member -> !StringUtils
                    .equals(member.getId(), turnContext.getActivity().getRecipient().getId())
            ).map(channel -> turnContext.sendActivity(MessageFactory.text("欢迎使用酒店评论情感分析系统，请输入你的评论。")))
            .collect(CompletableFutures.toFutureList()).thenApply(resourceResponses -> null);
    }
}
