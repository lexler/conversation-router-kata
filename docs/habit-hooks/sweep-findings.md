# Habit Hooks sweep findings

Potential product bugs found while refactoring `RoutingRules`. None of them is fixed: the sweep only refactors, and the acceptance test pins today's behavior.

## Conversations routed to no queue

A verified conversation whose exec tag or exec mention isn't FalconAir, DuckWings or GR gets an empty queue ID and the routing message `Verification complete. `. 2,184 combinations in the approved file end like this, for example an exec tag of `NO_EXEC_MENTION_TAG`. The exec checks don't fall through to the next rule.

A customer who isn't bound and whose verification status is anything other than none, expired, submitted or bypass also gets an empty queue ID.

## Routing messages that name the wrong queue

- Unknown brand on the Cyprus page: the message says "Falcon Care-Facebook Private queue", the conversation goes to the Cyprus queue (`cy.facebook`).
- Unknown brand on a Business page: the message says "Falcon Care-Facebook" or "Falcon Care-Instagram", the conversation goes to the Business queues (`biz.facebook`, `biz.instagram`).

## Verification status matching

Expired, submitted and bypass are matched with a case-sensitive `contains`, while "no status" is matched with `equalsIgnoreCase`. A status such as `Submitted` or `not submitted` routes differently from what its name suggests.

## Error message keeps the verification note

When the queue lookup fails after verification, the routing message reads `Verification complete. An error occurred assigning workQueue...`.

## Disruption mentions outside FalconAir's home market

Only FalconAir and unknown-brand customers on home-market pages reach the Disruption team. DuckWings customers and every other page ignore the disruption mention. This may be intended, since the Disruption team belongs to FalconAir.

## Channels other than Facebook and Instagram

A channel that is neither Facebook nor Instagram goes to a Facebook queue for some rules (unknown brand on Greece or Business pages) and to the home market for others (DuckWings and FalconAir customers on French, Greece, Cyprus or Business pages).
