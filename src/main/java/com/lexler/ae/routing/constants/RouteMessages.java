package com.lexler.ae.routing.constants;

public class RouteMessages {

    public static final String NOT_BOUND_BOT_QUEUE = "Conversation is not bound, assigning to Bot queue. Verification initiated. ";

    //error message
    public static final String ERROR_MESSAGE = "An error occurred assigning workQueue. Sending to Facebook Falcon Care queue by default.";

    //exec
    public static final String AUTHOR_EXEC_FALCONAIR = "Author has FalconAir exec tag. Assigning to Exec Private queue ";
    public static final String AUTHOR_EXEC_DUCKWINGS = "Author has DuckWings exec tag. Routing to DuckWings ESC-Private queue ";
    public static final String AUTHOR_EXEC_GR = "Author has Greece exec tag. Routing to Greece-Exec queue ";
    public static final String EXEC_MENTION_FALCONAIR = "Conversation has FalconAir exec mention tags. Routing to Exec Private queue ";
    public static final String EXEC_MENTION_DUCKWINGS = "Conversation has DuckWings exec mention tags. Routing to DuckWings ESC-Private queue ";
    public static final String EXEC_MENTION_GR = "Conversation has Greece exec mentions. Routing to Greece-Exec queue ";

    //no customer type found
    public static final String NO_CUSTOMER_BRAND_FRENCH_PAGE = "Customer brand was not identified default FalconAir. Routing to Falcon Care-French Private queue ";
    public static final String NO_CUSTOMER_BRAND_GR_INSTAGRAM = "Customer brand was not identified default FalconAir. Routing to Greece-Instagram Private queue ";
    public static final String NO_CUSTOMER_BRAND_GR_FB = "Customer brand was not identified default FalconAir. Routing to Greece-Facebook Private queue ";
    public static final String NO_CUSTOMER_BRAND_INSTAGRAM = "Customer brand was not identified default FalconAir. Routing to Falcon Care-Instagram Private queue ";
    public static final String NO_CUSTOMER_BRAND_FB = "Customer brand was not identified default FalconAir. Routing to Falcon Care-Facebook Private queue ";

    public static final String NO_CUSTOMER_BRAND_CY_FB = "Customer brand was not identified default FalconAir. Routing to Falcon Care-Facebook Private queue ";
    public static final String NO_CUSTOMER_BRAND_BIZ_FB = "Customer brand was not identified. Default routing to Falcon Care-Facebook Private queue ";
    public static final String NO_CUSTOMER_BRAND_BIZ_INSTAGRAM = "Customer brand was not identified. Default routing to Falcon Care-Instagram Private queue ";

    //duckwings customer type
    public static final String DUCKWINGS_CUSTOMER_FRENCH_INSTAGRAM = "DuckWings french customer. Routing to DuckWings French-Instagram Private queue ";
    public static final String DUCKWINGS_CUSTOMER_FRENCH_FB = "DuckWings french customer. Routing to DuckWings French-Facebook Private queue ";
    public static final String DUCKWINGS_CUSTOMER_FB = "DuckWings customer. Routing to DuckWings Care-Facebook Private queue ";
    public static final String DUCKWINGS_CUSTOMER_INSTAGRAM = "DuckWings customer. Routing to DuckWings Care-Instagram Private queue ";
    public static final String DUCKWINGS_CUSTOMER_GR_INSTAGRAM = "DuckWings Greece customer. Routing to DuckWings Greece-Instagram Private queue ";
    public static final String DUCKWINGS_CUSTOMER_GR_FB = "DuckWings Greece customer. Routing to DuckWings Greece-Facebook Private queue ";

    public static final String DUCKWINGS_CUSTOMER_CY_FB = "DuckWings Cyprus customer. Routing to DuckWings Cyprus Facebook Private queue ";

    //falcon customer type
    public static final String FALCONAIR_CUSTOMER = "FalconAir customer. ";
    public static final String FALCONAIR_CUSTOMER_FRENCH = "FalconAir french customer. Routing to Falcon Care-French Private queue ";
    public static final String FALCONAIR_CUSTOMER_FB = FALCONAIR_CUSTOMER + "Routing to Falcon Care - Facebook Private queue ";
    public static final String FALCONAIR_CUSTOMER_INSTAGRAM = FALCONAIR_CUSTOMER + "Routing to Falcon Care -Instagram Private queue ";
    public static final String FALCONAIR_CUSTOMER_GR_FB = "FalconAir Greece customer. Routing to Greece-Facebook Private queue ";
    public static final String FALCONAIR_CUSTOMER_GR_INSTAGRAM = "FalconAir Greece customer. Routing to Greece-Instagram Private queue ";

    public static final String FALCONAIR_CUSTOMER_CY_FB = "FalconAir Cyprus customer. Routing to Greece Cyprus Private queue ";
    public static final String FALCONAIR_CUSTOMER_BIZ_FB = "FalconAir Business customer. Routing to BIZ Facebook Private queue ";
    public static final String FALCONAIR_CUSTOMER_BIZ_INSTAGRAM = "FalconAir Business customer. Routing to BIZ Instagram Private queue ";

    public static final String VERIFIED = "Verification complete. ";

    //disruption
    public static final String DISRUPTION_MENTION = "Conversation has Disruption mention tag. Routing to Disruption queue - ";

    //channels
    public static final String FACEBOOK_CHANNEL = "Facebook ";
    public static final String INSTAGRAM_CHANNEL = "Instagram ";

}