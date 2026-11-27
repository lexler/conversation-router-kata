package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.config.RoutingConfig;
import com.lexler.kata.routingrules.event.BotEvent;
import com.lexler.kata.routingrules.log.Log;
import com.lexler.kata.routingrules.log.LogMessage;

import static com.lexler.kata.routingrules.Page.Audience.*;
import static com.lexler.kata.routingrules.constants.ConstantStrings.*;
import static com.lexler.kata.routingrules.constants.LogParams.*;
import static com.lexler.kata.routingrules.constants.RouteMessages.*;
import static com.lexler.kata.routingrules.log.LogMessage.message;

public class RoutingRules {

    private static final Log log = Log.forClass(RoutingRules.class);

    private final RoutingConfig config;
    private final Pages pages;

    public RoutingRules(RoutingConfig config) {
        this.config = config;
        this.pages = new Pages(config);
    }

    public QueueInfo getAssignedWorkQueue(BotEvent event) {

        log.info(conversationMessage(event, "Determining workQueue the conversation belongs to", "getAssignedWorkQueue"));

        String pageQueue = "";
        StringBuilder messageBuilder = new StringBuilder(130);

        try {
            Page page = pages.pageAt(event.coordinate());
            log.debug(conversationMessage(event, "Page: " + page, "getAssignedWorkQueue")
                .param(PAGE_ID, event.coordinate().externalId()));

            Conversation conversation = Conversation.of(event, page);

            //route to bot queue if convo is not bound and needs to wait on timer function
            if (conversation.awaitsVerification()) {
                log.info(conversationMessage(event, "Conversation is not bound, and status is empty or null. Assigning to the bot queue", "getAssignedWorkQueue")
                    .param(BIND_STATUS, conversation.isBound())
                    .param(EVENT_STATUS, conversation.verificationStatus()));

                pageQueue = config.getProperty("bot.queue");
                messageBuilder.append(NOT_BOUND_BOT_QUEUE);

                //if convo is bound or is timer function event
            } else if (conversation.isReadyForSupportTeam()) {
                log.info(conversationMessage(event, "Conversation is either bound, bypassed or the timer expired. Assigning it to a new workQueue.", "getAssignedWorkQueue")
                    .param(BIND_STATUS, conversation.isBound())
                    .param(EVENT_STATUS, conversation.verificationStatus()));

                //if already bound/or completed bind add verified message to output message
                if (conversation.isVerified()) {
                    messageBuilder.append(VERIFIED);
                }

                //check if author has exec tags, put into exec queue according to which author tag is added. customer type does not indicate queue in this case.
                if (conversation.hasExecTag()) {
                    log.info(conversationMessage(event, "Conversation has Author Exec tags. Assigning to one of the exec queues", "getAssignedWorkQueue")
                        .param(BIND_STATUS, conversation.isBound())
                        .param(EVENT_STATUS, conversation.verificationStatus()));
                    if (FALCONAIR.equalsIgnoreCase(conversation.execTag())) {
                        pageQueue = config.getProperty("falcon.exec.queue");
                        messageBuilder.append(AUTHOR_EXEC_FALCONAIR);
                    } else if (DUCKWINGS.equalsIgnoreCase(conversation.execTag())) {
                        pageQueue = config.getProperty("duckwings.exec.queue");
                        messageBuilder.append(AUTHOR_EXEC_DUCKWINGS);
                    } else if (GR.equalsIgnoreCase(conversation.execTag())) {
                        pageQueue = config.getProperty("gr.exec.queue");
                        messageBuilder.append(AUTHOR_EXEC_GR);
                    }
                    //check if convo has exec tags put into exec queue according to which exec is mentioned. customer type does not indicate queue in this case.
                } else if (conversation.hasExecMention()) {
                    log.info(conversationMessage(event, "Conversation has Exec mention tags.", "getAssignedWorkQueue")
                        .param(EXEC_MENTION, conversation.execMention()));

                    if (FALCONAIR.equalsIgnoreCase(conversation.execMention())) {
                        pageQueue = config.getProperty("falcon.exec.queue");
                        messageBuilder.append(EXEC_MENTION_FALCONAIR);
                    } else if (DUCKWINGS.equalsIgnoreCase(conversation.execMention())) {
                        pageQueue = config.getProperty("duckwings.exec.queue");
                        messageBuilder.append(EXEC_MENTION_DUCKWINGS);
                    } else if (GR.equalsIgnoreCase(conversation.execMention())) {
                        pageQueue = config.getProperty("gr.exec.queue");
                        messageBuilder.append(EXEC_MENTION_GR);
                    }

                    //if no exec tags are present in conversation& author, assign based on customerType and pageId
                } else if (conversation.hasUnknownBrand()) {
                    log.info(conversationMessage(event, "Conversation has no customer brand. Assigning default falconair queue based on network type and pageid.", "getAssignedWorkQueue")
                        .param(CUSTOMER_BRAND, conversation.brand()));
                    //this indicated either a failure in LoyaltyHub api call, customer was not found, or we have not reAuthed person yet and no DB info was found
                    if (page.serves(FRENCH_SPEAKERS)) {
                        pageQueue = config.getProperty("falcon.french.care");
                        messageBuilder.append(NO_CUSTOMER_BRAND_FRENCH_PAGE);
                    } else if (page.serves(GREECE)) {
                        if (page.isOnInstagram()) {
                            pageQueue = config.getProperty("gr.instagram");
                            messageBuilder.append(NO_CUSTOMER_BRAND_GR_INSTAGRAM);
                        } else {
                            pageQueue = config.getProperty("gr.facebook");
                            messageBuilder.append(NO_CUSTOMER_BRAND_GR_FB);
                        }
                    } else if (page.serves(CYPRUS)) {
                        pageQueue = config.getProperty("cy.facebook");
                        messageBuilder.append(NO_CUSTOMER_BRAND_CY_FB);
                    } else if (page.serves(BUSINESS)) {
                        if (page.isOnInstagram()) {
                            pageQueue = config.getProperty("biz.instagram");
                            messageBuilder.append(NO_CUSTOMER_BRAND_BIZ_INSTAGRAM);
                        } else {
                            pageQueue = config.getProperty("biz.facebook");
                            messageBuilder.append(NO_CUSTOMER_BRAND_BIZ_FB);
                        }
                    } else if (page.isOnInstagram()) {
                        if (conversation.mentionsDisruption()) {
                            pageQueue = config.getProperty("falcon.instagram.disruption");
                            messageBuilder.append(DISRUPTION_MENTION);
                            messageBuilder.append(INSTAGRAM_CHANNEL);
                        } else {
                            pageQueue = config.getProperty("falcon.instagram.care");
                            messageBuilder.append(NO_CUSTOMER_BRAND_INSTAGRAM);
                        }
                    } else {//falcon and fb channel
                        if (conversation.mentionsDisruption()) {
                            pageQueue = config.getProperty("falcon.fb.disruption");
                            messageBuilder.append(DISRUPTION_MENTION);
                            messageBuilder.append(FACEBOOK_CHANNEL);
                        } else {
                            pageQueue = config.getProperty("falcon.fb.care");
                            messageBuilder.append(NO_CUSTOMER_BRAND_FB);
                        }
                    }
                } else {//at this point expecting customer brand and/or bind/status details to be present without exect tags
                    log.info(conversationMessage(event, "Checking customer brand.", "getAssignedWorkQueue"));

                    if (conversation.isDuckWingsCustomer()) {
                        log.info(conversationMessage(event, "Customer identified as DuckWings customer", "getAssignedWorkQueue"));
                        if (page.serves(FRENCH_SPEAKERS) && page.isOnInstagram()) {
                            pageQueue = config.getProperty("duck.french.instagram");
                            messageBuilder.append(DUCKWINGS_CUSTOMER_FRENCH_INSTAGRAM);
                        } else if (page.serves(FRENCH_SPEAKERS) && page.isOnFacebook()) {
                            pageQueue = config.getProperty("duck.french.fb");
                            messageBuilder.append(DUCKWINGS_CUSTOMER_FRENCH_FB);
                        } else if (page.serves(GREECE) && page.isOnInstagram()) {
                            pageQueue = config.getProperty("duck.gr.instagram");
                            messageBuilder.append(DUCKWINGS_CUSTOMER_GR_INSTAGRAM);
                        } else if (page.serves(GREECE) && page.isOnFacebook()) {
                            pageQueue = config.getProperty("duck.gr.fb");
                            messageBuilder.append(DUCKWINGS_CUSTOMER_GR_FB);
                        } else if (page.serves(CYPRUS) && page.isOnFacebook()) {
                            pageQueue = config.getProperty("duck.cy.facebook");
                            messageBuilder.append(DUCKWINGS_CUSTOMER_CY_FB);
                        } else if (page.isOnInstagram()) {
                            pageQueue = config.getProperty("duck.care.instagram");
                            messageBuilder.append(DUCKWINGS_CUSTOMER_INSTAGRAM);
                        } else {
                            pageQueue = config.getProperty("duck.care.fb");
                            messageBuilder.append(DUCKWINGS_CUSTOMER_FB);
                        }
                    } else {//customer is falconair or prospect (or not in merged loyalty DB yet)
                        log.info(conversationMessage(event, "Customer identified as non DuckWings customer", "getAssignedWorkQueue")
                            .param(EVENT_STATUS, conversation.verificationStatus())
                            .param(CUSTOMER_BRAND, conversation.brand()));
                        if (page.serves(FRENCH_SPEAKERS)) {
                            pageQueue = config.getProperty("falcon.french.care");
                            messageBuilder.append(FALCONAIR_CUSTOMER_FRENCH);
                        } else if (page.serves(GREECE) && page.isOnInstagram()) {
                            pageQueue = config.getProperty("gr.instagram");
                            messageBuilder.append(FALCONAIR_CUSTOMER_GR_INSTAGRAM);
                        } else if (page.serves(GREECE) && page.isOnFacebook()) {
                            pageQueue = config.getProperty("gr.facebook");
                            messageBuilder.append(FALCONAIR_CUSTOMER_GR_FB);
                        } else if (page.serves(CYPRUS) && page.isOnFacebook()) {
                            pageQueue = config.getProperty("cy.facebook");
                            messageBuilder.append(FALCONAIR_CUSTOMER_CY_FB);
                        } else if (page.serves(BUSINESS) && page.isOnFacebook()) {
                            pageQueue = config.getProperty("biz.facebook");
                            messageBuilder.append(FALCONAIR_CUSTOMER_BIZ_FB);
                        } else if (page.serves(BUSINESS) && page.isOnInstagram()) {
                            pageQueue = config.getProperty("biz.instagram");
                            messageBuilder.append(FALCONAIR_CUSTOMER_BIZ_INSTAGRAM);
                        } else if (page.isOnInstagram()) {
                            if (conversation.mentionsDisruption()) {
                                pageQueue = config.getProperty("falcon.instagram.disruption");
                                messageBuilder.append(DISRUPTION_MENTION);
                                messageBuilder.append(INSTAGRAM_CHANNEL);
                            } else {
                                pageQueue = config.getProperty("falcon.instagram.care");
                                messageBuilder.append(FALCONAIR_CUSTOMER_INSTAGRAM);
                            }

                        } else {//falcon and fb channel
                            if (conversation.mentionsDisruption()) {
                                pageQueue = config.getProperty("falcon.fb.disruption");
                                messageBuilder.append(DISRUPTION_MENTION);
                                messageBuilder.append(FACEBOOK_CHANNEL);
                            } else {
                                pageQueue = config.getProperty("falcon.fb.care");
                                messageBuilder.append(FALCONAIR_CUSTOMER_FB);
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(message("An error occurred when trying to assign work queue id. Assigning default values", "getAssignedWorkQueue")
                .param(EVENT, event)
                .param(EXCEPTION, e.getMessage()));
            //set default values if any errors assigning work queue above
            pageQueue = config.getProperty("falcon.fb.care");
            messageBuilder.append(ERROR_MESSAGE);
        }
        messageBuilder.append(pageQueue);

        QueueInfo assignedQueueInfo = new QueueInfo(messageBuilder.toString(), pageQueue);
        log.info(message("Work Queue assigned to this conversation: " + assignedQueueInfo.queueId(), "getAssignedWorkQueue")
            .param(CONVERSATION_ID, event.conversationId()).param(WORKQUEUE_ID, assignedQueueInfo.queueId()).param(MESSAGE, assignedQueueInfo.message()).param(CHANNEL_ID, event.channelId()).param(REQUEST_ID, event.requestId()));
        return assignedQueueInfo;
    }

    private static LogMessage conversationMessage(BotEvent event, String text, String method) {
        return message(text, method)
            .param(CONVERSATION_ID, event.conversationId())
            .param(REQUEST_ID, event.requestId())
            .param(CHANNEL_ID, event.channelId());
    }
}
