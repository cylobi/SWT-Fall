- Why 100% mutation coverage might not be possible.
- We reached 100% mutation coverage for Transaction but not the TransactionEngine
- Reasons Why 100% mutation coverage for TransactionEngine might not be possible:
  1. No semantic changes of some mutations
  2. hardcoded constants like 'Threshold' can have limited variations and tests can't cover them.