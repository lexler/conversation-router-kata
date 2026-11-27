# Terms

The language of FalconAir's conversation router, which decides where each customer conversation from social media goes.

## Airlines and customers

**FalconAir**:
The main airline. It recently bought DuckWings. A customer whose brand is unknown is served as a FalconAir customer.
_In code_: `FalconAir`, `falcon`

**DuckWings**:
The airline FalconAir bought. Its customers keep their own support teams until the loyalty programs merge.
_In code_: `DuckWings`, `duck`, `duckwings`

**Customer**:
A person who writes to one of the airlines' pages.
_Avoid_: traveler, author, user

**Brand**:
The airline whose loyalty program a customer belongs to: FalconAir, DuckWings, or none when LoyaltyHub can't tell.
_Avoid_: customer type

**LoyaltyHub**:
The system that knows each customer's brand, across both airlines.

## Verification

**Verification**:
Linking a customer's social account to their loyalty account, so the router knows who they are. The bot runs it inside the conversation.

**Bound**:
A customer who has finished verification.
_In code_: `bind`, `bound`

**Verification status**:
Where verification stands for a customer who isn't bound yet: none, submitted (the customer completed it), expired (the customer didn't finish in time) or bypass (verification skipped).
_In code_: `status`

## Where conversations come from

**Conversation**:
The messages between one customer and one page.
_Avoid_: convo, event

**Channel**:
The social network a conversation comes from: Facebook or Instagram. Facebook is often shortened to `fb` in the code.
_In code_: `networkKey`, `network`
_Avoid_: network

**Page**:
One airline account on one channel. Each page serves the home market, a language, another market or business customers.
_In code_: `pageId`, `externalId`

**Home market**:
The default market, served in English. It has no name in the code: every page that isn't French, Greece, Cyprus or Business belongs to it.

**French pages**:
Pages for French-speaking customers.
_In code_: `french`, `fr`

**Greece**:
A market with its own pages and its own exec team.
_In code_: `GR`, `gr`

**Cyprus**:
A smaller Greek-speaking market next to Greece, with one FalconAir Facebook page.
_In code_: `cy`

**Business pages**:
FalconAir pages for corporate customers.
_In code_: `biz`

## Where conversations go

**Queue**:
Where a conversation waits for an agent. Each queue belongs to one support team.
_In code_: `queueId`, `workQueue`, `pageQueue`

**Support team**:
A team of customer support agents who answer customers in writing, through the airlines' pages on social media. Each team works its own queues.
_Avoid_: desk, care desk

**Falcon Care**:
FalconAir's main support team for the home market.
_In code_: `care`

**DuckWings Care**:
DuckWings's main support team for the home market.

**Exec team**:
The support team for VIP customers and for conversations that mention an executive. FalconAir, DuckWings and Greece each have one. DuckWings calls its exec team ESC.

**Disruption team**:
The FalconAir support team for conversations about a disruption, such as a delay or a cancellation.

**Bot queue**:
Where the bot holds a conversation while it verifies the customer.

**Routing message**:
The explanation the router attaches to each routing decision. It ends with the queue ID.

## Tags

**Exec tag**:
Marks the customer as a VIP of FalconAir, DuckWings or Greece.
_In code_: `authorExecTag`, `authorTag`

**Exec mention**:
Marks a conversation that mentions an executive of FalconAir, DuckWings or Greece.
_In code_: `execMentionTag`, `execMention`

**Disruption mention**:
Marks a conversation that mentions a disruption.
_In code_: `disruptionMentionTag`, `disruptionTag`
