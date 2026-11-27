# Conversation router

FalconAir recently bought DuckWings. Both airlines want customers to get help in a chat instead of on the phone. So each airline has pages on Facebook and Instagram, where it posts news and offers, and where customers write to it about a booking, a delay or their loyalty points.

One bot answers on every page of both airlines. It asks the customer to verify by linking their social account to their loyalty account. After that, a person from a support team takes over.

Until the loyalty programs merge, each airline keeps its own support teams. A customer doesn't always write to their own airline's page, so the page alone doesn't say who should help. For each conversation, the conversation router works out:

- whether the customer is verified yet: if not, the conversation stays in the bot queue
- whose customer it is: FalconAir, DuckWings or unknown, as LoyaltyHub reports it
- where the customer wrote: the channel, and whether the page serves the home market, French speakers, Greece, Cyprus or business customers
- whether it needs special care: a VIP customer, a mention of an executive, or a disruption such as a delay

From that, the router picks the queue the conversation waits in and writes a routing message that says why.

The routing rules grew one exception at a time. They now live in one method, `RoutingRules.getAssignedWorkQueue`, and the developers are afraid to touch it.

The domain language is in `docs/terms.md`.
