# Advancements Cascade

⚠️ This mod doesn't change anything to the game on its own

This mod adds advancement triggers, for mods, modpacks and datapacks to use, that fires when an advancement is either gained or lost, with the following IDs: `advancement_cascade:advancement_gain` and `advancement_cascade:advancement_loss`

These can be use as such:
```json
├ type: one of "only",or "any".
├ target: if the type is "only", an advancement ID or an array of advancement ID upon which to trigger the criterion.
└ predicate: a Player predicate, based off of loot conditions, to be validated to trigger the criterion.
```

Please refer to the wiki's [advancement](https://minecraft.wiki/w/Advancement/JSON_format) and [predicate](https://minecraft.wiki/w/Predicate) pages for more details on their respective format.