[x] Write smallest possible approval test that Claude can come up with
[x] What are possible approval test scenarios. I want to understand the behavior of the Routing rules class.
[x] Extract the storyboard so that it is reusable for other example tests similar as in https://github.com/LearnWithLlew/TestingBetterWithApprovals.Java/blob/main/src/test/java/org/learnwithllew/week7/Week7.testConversations.approved.md
[ ] I would like to understand the scenario from looking at the storyboard at a first glance

Okay, here's the deal. I don't like that the conversation has the same weight as everything else, so I don't understand what's businessy importance and what's not. I don't care about the QID for the business information. Why are there two fields called router? That's confusing. What does exec mean? That I don't understand. I am only interested in customer information that is relevant to the test case  
Don't implement yet. Come back to me with a couple of suggestions so I can cherry pick what I like and what I don't.

[Conversation]: facebook page 5001
[    Customer]: brand none, not bound, verification status none
[        Tags]: exec tag none, exec mention none, disruption mention none
[      Router]: queue 100
[      Router]: Conversation is not bound, assigning to Bot queue. Verification initiated. 100