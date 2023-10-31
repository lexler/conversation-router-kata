package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.config.RoutingConfig;
import com.lexler.kata.routingrules.event.BotEvent;
import com.lexler.kata.routingrules.event.CustomerInfo;
import com.lexler.kata.routingrules.log.Log;
import com.lexler.kata.routingrules.util.StringUtils;

import static com.lexler.kata.routingrules.constants.ConstantStrings.*;
import static com.lexler.kata.routingrules.constants.DefaultStrings.*;
import static com.lexler.kata.routingrules.constants.LogParams.*;
import static com.lexler.kata.routingrules.constants.RouteMessages.*;
import static com.lexler.kata.routingrules.log.LogMessage.message;

public class RoutingRules {

    private static final Log log = Log.forClass(RoutingRules.class);

    private final RoutingConfig config;

    public RoutingRules(RoutingConfig config) {
        this.config = config;
    }

    public QueueInfo getAssignedWorkQueue(BotEvent event, String conversationId, String requestId, String channelId) {
        log.info(message("Determining workQueue the conversation belongs to", "getAssignedWorkQueue").add(event.toLogContext()));
        try {
            RoutingContext context = routingInfo(event, conversationId, requestId, channelId);

            if (isUnauthedNewConveration(context)) {
                return routeToBot(event, context);
            }

            log.info(message("Conversation is either bound, bypassed or the timer expired. Assigning it to an agent workQueue.", "getAssignedWorkQueue")
                .param(BIND_STATUS, context.bound()).param(EVENT_STATUS, context.status()).add(event.toLogContext()));

            if (authorHasExecTag(context)) {
                return sendToVipQueue(event, context);
            }

            StringBuilder messageBuilder = new StringBuilder(130);
            //if already bound/or completed bind add verified message to output message
            if (context.bound() || context.status().contains(SUBMITTED)) {
                messageBuilder.append(VERIFIED);
            }
            String pageQueue = "";

            if (context.french()) {
                //if no exec tags are present in conversation& author, assign based on customerType and pageId
                if (context.customerBrand().isEmpty() || NO_CUSTOMER_BRAND.equalsIgnoreCase(context.customerBrand())) {
                    log.info(message("Conversation has no customer brand. Assigning default falconair queue based on network type and pageid.", "getAssignedWorkQueue")
                        .param(CUSTOMER_BRAND, context.customerBrand())
                        .add(event.toLogContext()));

                    //this indicated either a failure in LoyaltyHub api call, customer was not found, or we have not reAuthed person yet and no DB info was found
                    pageQueue = config.getProperty("falcon.french.care");
                    messageBuilder.append(NO_CUSTOMER_BRAND_FRENCH_PAGE);
                    return returnResult(event, messageBuilder, pageQueue);
                } else {
                    //at this point expecting customer brand and/or bind/status details to be present without exect tags
                    log.info(message("Checking customer brand.", "getAssignedWorkQueue")
                        .add(event.toLogContext()));

                    //customer is falconair or prospect (or not in merged loyalty DB yet)
                    log.info(message("Customer identified as non DuckWings customer", "getAssignedWorkQueue")
                        .param(EVENT_STATUS, context.status())
                        .param(CUSTOMER_BRAND, context.customerBrand())
                        .add(event.toLogContext()));
                    if (context.french()) {
                        pageQueue = config.getProperty("falcon.french.care");
                        messageBuilder.append(FALCONAIR_CUSTOMER_FRENCH);
                    }
                    return returnResult(event, messageBuilder, pageQueue);
                }
            } else {
                //if no exec tags are present in conversation& author, assign based on customerType and pageId
                if (context.customerBrand().isEmpty() || NO_CUSTOMER_BRAND.equalsIgnoreCase(context.customerBrand())) {

                    log.info(message("Conversation has no customer brand. Assigning default falconair queue based on network type and pageid.", "getAssignedWorkQueue")
                        .param(CUSTOMER_BRAND, context.customerBrand())
                        .add(event.toLogContext()));

                    //this indicated either a failure in LoyaltyHub api call, customer was not found, or we have not reAuthed person yet and no DB info was found
                    if (context.french()) {
                        pageQueue = config.getProperty("falcon.french.care");
                        messageBuilder.append(NO_CUSTOMER_BRAND_FRENCH_PAGE);
                    } else if (context.greece()) {
                        if (INSTAGRAM.equalsIgnoreCase(context.network())) {
                            pageQueue = config.getProperty("gr.instagram");
                            messageBuilder.append(NO_CUSTOMER_BRAND_GR_INSTAGRAM);
                        } else {
                            pageQueue = config.getProperty("gr.facebook");
                            messageBuilder.append(NO_CUSTOMER_BRAND_GR_FB);
                        }
                    } else if (context.cyprus()) {
                        pageQueue = config.getProperty("cy.facebook");
                        messageBuilder.append(NO_CUSTOMER_BRAND_CY_FB);
                    } else if (context.biz()) {
                        if (INSTAGRAM.equalsIgnoreCase(context.network())) {
                            pageQueue = config.getProperty("biz.instagram");
                            messageBuilder.append(NO_CUSTOMER_BRAND_BIZ_INSTAGRAM);
                        } else {
                            pageQueue = config.getProperty("biz.facebook");
                            messageBuilder.append(NO_CUSTOMER_BRAND_BIZ_FB);
                        }
                    } else if (INSTAGRAM.equalsIgnoreCase(context.network())) {
                        if (DISRUPTION.equalsIgnoreCase(context.disruptionTag())) {
                            pageQueue = config.getProperty("falcon.instagram.disruption");
                            messageBuilder.append(DISRUPTION_MENTION);
                            messageBuilder.append(INSTAGRAM_CHANNEL);
                        } else {
                            pageQueue = config.getProperty("falcon.instagram.care");
                            messageBuilder.append(NO_CUSTOMER_BRAND_INSTAGRAM);
                        }
                    } else {//falcon and fb channel
                        if (DISRUPTION.equalsIgnoreCase(context.disruptionTag())) {
                            pageQueue = config.getProperty("falcon.fb.disruption");
                            messageBuilder.append(DISRUPTION_MENTION);
                            messageBuilder.append(FACEBOOK_CHANNEL);
                        } else {
                            pageQueue = config.getProperty("falcon.fb.care");
                            messageBuilder.append(NO_CUSTOMER_BRAND_FB);
                        }
                    }
                    return returnResult(event, messageBuilder, pageQueue);
                } else {
                    //at this point expecting customer brand and/or bind/status details to be present without exect tags
                    log.info(message("Checking customer brand.", "getAssignedWorkQueue")
                        .add(event.toLogContext()));

                    //customer is falconair or prospect (or not in merged loyalty DB yet)
                    log.info(message("Customer identified as non DuckWings customer", "getAssignedWorkQueue")
                        .param(EVENT_STATUS, context.status())
                        .param(CUSTOMER_BRAND, context.customerBrand())
                        .add(event.toLogContext()));
                    if (context.french()) {
                        pageQueue = config.getProperty("falcon.french.care");
                        messageBuilder.append(FALCONAIR_CUSTOMER_FRENCH);
                    } else if (context.greece() && INSTAGRAM.equalsIgnoreCase(context.network())) {
                        pageQueue = config.getProperty("gr.instagram");
                        messageBuilder.append(FALCONAIR_CUSTOMER_GR_INSTAGRAM);
                    } else if (context.greece() && FACEBOOK.equalsIgnoreCase(context.network())) {
                        pageQueue = config.getProperty("gr.facebook");
                        messageBuilder.append(FALCONAIR_CUSTOMER_GR_FB);
                    } else if (context.cyprus() && FACEBOOK.equalsIgnoreCase(context.network())) {
                        pageQueue = config.getProperty("cy.facebook");
                        messageBuilder.append(FALCONAIR_CUSTOMER_CY_FB);
                    } else if (context.biz() && FACEBOOK.equalsIgnoreCase(context.network())) {
                        pageQueue = config.getProperty("biz.facebook");
                        messageBuilder.append(FALCONAIR_CUSTOMER_BIZ_FB);
                    } else if (context.biz() && INSTAGRAM.equalsIgnoreCase(context.network())) {
                        pageQueue = config.getProperty("biz.instagram");
                        messageBuilder.append(FALCONAIR_CUSTOMER_BIZ_INSTAGRAM);
                    } else if (INSTAGRAM.equalsIgnoreCase(context.network())) {
                        if (DISRUPTION.equalsIgnoreCase(context.disruptionTag())) {
                            pageQueue = config.getProperty("falcon.instagram.disruption");
                            messageBuilder.append(DISRUPTION_MENTION);
                            messageBuilder.append(INSTAGRAM_CHANNEL);
                        } else {
                            pageQueue = config.getProperty("falcon.instagram.care");
                            messageBuilder.append(FALCONAIR_CUSTOMER_INSTAGRAM);
                        }

                    } else {//falcon and fb channel
                        if (DISRUPTION.equalsIgnoreCase(context.disruptionTag())) {
                            pageQueue = config.getProperty("falcon.fb.disruption");
                            messageBuilder.append(DISRUPTION_MENTION);
                            messageBuilder.append(FACEBOOK_CHANNEL);
                        } else {
                            pageQueue = config.getProperty("falcon.fb.care");
                            messageBuilder.append(FALCONAIR_CUSTOMER_FB);
                        }

                    }
                    return returnResult(event, messageBuilder, pageQueue);
                }
            }

        } catch (Exception e) {
            log.error(message("An error occurred when trying to assign work queue id. Assigning default values", "getAssignedWorkQueue")
                .param(EVENT, event)
                .param(EXCEPTION, e.getMessage()));
            //set default values if any errors assigning work queue above

            String defaultQueueId = config.getProperty("falcon.fb.care");
            return new QueueInfo(ERROR_MESSAGE + defaultQueueId, defaultQueueId);
        }
    }

    private QueueInfo routeToBot(BotEvent event, RoutingContext context) {
        log.info(message("Conversation is not bound, and status is empty or null. Assigning to the bot queue", "getAssignedWorkQueue")
            .param(BIND_STATUS, false)
            .param(EVENT_STATUS, context.status())
            .add(event.toLogContext()));
        return returnResult(event, new StringBuilder().append(NOT_BOUND_BOT_QUEUE), config.getProperty("bot.queue"));
    }

    private boolean isUnauthedNewConveration(RoutingContext context) {
        return !context.bound() && NO_STATUS.equalsIgnoreCase(context.status());
    }

    private QueueInfo sendToVipQueue(BotEvent event, RoutingContext context) {
        StringBuilder notification = new StringBuilder(130);
        if (context.bound() || context.status().contains(SUBMITTED)) {
            notification.append(VERIFIED);
        }

        log.info(message("Conversation has Author Exec tags. Assigning to one of the exec queues", "getAssignedWorkQueue")
            .param(BIND_STATUS, context.bound())
            .param(EVENT_STATUS, context.status())
            .add(event.toLogContext()));

        String pageQueue = "";
        if (FALCONAIR.equalsIgnoreCase(context.authorTag())) {
            pageQueue = config.getProperty("falcon.exec.queue");
            notification.append(AUTHOR_EXEC_FALCONAIR);
        } else if (DUCKWINGS.equalsIgnoreCase(context.authorTag())) {
            pageQueue = config.getProperty("duckwings.exec.queue");
            notification.append(AUTHOR_EXEC_DUCKWINGS);
        } else if (GR.equalsIgnoreCase(context.authorTag())) {
            pageQueue = config.getProperty("gr.exec.queue");
            notification.append(AUTHOR_EXEC_GR);
        }
        return returnResult(event, notification, pageQueue);
    }

    private boolean authorHasExecTag(RoutingContext context) {
        return !context.authorTag().equalsIgnoreCase(NO_AUTHOR_EXEC_TAG);
    }

    private QueueInfo returnResult(BotEvent event, StringBuilder messageBuilder, String queueId) {
        messageBuilder.append(queueId);

        QueueInfo assignedQueueInfo = new QueueInfo(messageBuilder.toString(), queueId);
        log.info(message("Work Queue assigned to this conversation: " + assignedQueueInfo.queueId(), "getAssignedWorkQueue")
            .param(WORKQUEUE_ID, assignedQueueInfo.queueId())
            .param(MESSAGE, assignedQueueInfo.message())
            .add(event.toLogContext()));
        return assignedQueueInfo;
    }

    private record RoutingContext(
        boolean bound, String status, String network, String authorTag, boolean french,
        String disruptionTag, String customerBrand, boolean greece,
        boolean cyprus,
        boolean biz
    ) {
    }

    private RoutingContext routingInfo(BotEvent event, String conversationId, String requestId, String channelId) {
        String externalId = event.coordinate().externalId();
        boolean french = isFrenchPage(externalId, requestId, conversationId, channelId);
        log.debug(message("French: " + french, "getAssignedWorkQueue")
            .param(PAGE_ID, externalId)
            .add(event.toLogContext()));

        boolean greece = isGreecePage(externalId, requestId, conversationId, channelId);
        log.debug(message("Greece: " + greece, "getAssignedWorkQueue")
            .param(PAGE_ID, externalId)
            .add(event.toLogContext()));

        boolean cyprus = isCyprus(externalId, requestId, conversationId, channelId);
        log.debug(message("Cyprus : " + cyprus, "getAssignedWorkQueue")
            .param(PAGE_ID, externalId)
            .add(event.toLogContext()));

        boolean biz = isBiz(externalId, requestId, conversationId, channelId);
        log.debug(message("FalconAir Business : " + biz, "getAssignedWorkQueue")
            .param(PAGE_ID, externalId)
            .add(event.toLogContext()));

        CustomerInfo customerInfo = event.customerInfo() == null ? new CustomerInfo() : event.customerInfo();
        boolean bound = customerInfo.bind();
        String status = StringUtils.isEmpty(customerInfo.status()) ? NO_STATUS : customerInfo.status();
        String authorTag = StringUtils.isEmpty(customerInfo.authorExecTag()) ? NO_AUTHOR_EXEC_TAG : customerInfo.authorExecTag();
        String disruptionTag = StringUtils.isEmpty(customerInfo.disruptionMentionTag()) ? NO_DISRUPTION_TAG : customerInfo.disruptionMentionTag();
        String customerBrand = StringUtils.isEmpty(customerInfo.customerBrand()) ? NO_CUSTOMER_BRAND : customerInfo.customerBrand();
        String network = event.coordinate().networkKey();
        return new RoutingContext(bound, status, network, authorTag, french, disruptionTag, customerBrand, greece, cyprus, biz);
    }

    public boolean isFrenchPage(String pageId, String requestId, String conversationId, String channelId) {
        log.debug(message("checking conversation page id if french page. " + pageId, "isFrenchPage")
            .param(CONVERSATION_ID, conversationId)
            .param(REQUEST_ID, requestId)
            .param(CHANNEL_ID, channelId));
        String falconInstagramFr = config.getProperty("falcon.instagram.fr.pageid");
        String falconFbFr = config.getProperty("falcon.fb.french.pageid");
        String duckInstagramFr = config.getProperty("duck.instagram.fr.pageid");
        String duckFbFr = config.getProperty("duck.fb.french.pagid");
        return pageId.equalsIgnoreCase(falconInstagramFr) || pageId.equalsIgnoreCase(falconFbFr) || pageId.equalsIgnoreCase(duckInstagramFr) || pageId.equalsIgnoreCase(duckFbFr);
    }

    public boolean isGreecePage(String pageId, String requestId, String conversationId, String channelId) {
        log.debug(message("checking page id if greece page. " + pageId, "isGreecePage")
            .param(CONVERSATION_ID, conversationId)
            .param(REQUEST_ID, requestId)
            .param(CHANNEL_ID, channelId));
        String falconInstagramGr = config.getProperty("falcon.instagram.gr.pageid");
        String falconFbGr = config.getProperty("falcon.fb.gr.pageid");
        String duckInstagramGr = config.getProperty("duck.instagram.gr.pageid");
        String duckFbGr = config.getProperty("duck.fb.gr.pagid");
        return pageId.equalsIgnoreCase(falconInstagramGr) || pageId.equalsIgnoreCase(falconFbGr) || pageId.equalsIgnoreCase(duckInstagramGr) || pageId.equalsIgnoreCase(duckFbGr);
    }

    public boolean isCyprus(String pageId, String requestId, String conversationId, String channelId) {
        log.debug(message("checking page id if cyprus page. " + pageId, "isCyprus")
            .param(CONVERSATION_ID, conversationId)
            .param(REQUEST_ID, requestId)
            .param(CHANNEL_ID, channelId));
        String falconFbCy = config.getProperty("falcon.facebook.cy.pageid");
        return pageId.equalsIgnoreCase(falconFbCy);
    }

    public boolean isBiz(String pageId, String requestId, String conversationId, String channelId) {
        log.debug(message("checking page id if Biz page. " + pageId, "isBiz")
            .param(CONVERSATION_ID, conversationId)
            .param(REQUEST_ID, requestId)
            .param(CHANNEL_ID, channelId));
        String falconInstagramBiz = config.getProperty("falcon.instagram.biz.pageid");
        String falconFbBiz = config.getProperty("falcon.fb.biz.pageid");
        return pageId.equalsIgnoreCase(falconInstagramBiz) || pageId.equalsIgnoreCase(falconFbBiz);
    }
}