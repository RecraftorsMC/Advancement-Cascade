# Advancements Cascade

⚠️ This mod doesn't change anything to the game on its own

This mod adds advancement triggers, for mods, modpacks and datapacks to use, that fires when an advancement is either gained or lost, with the following IDs: `advancement_cascade:advancement_gain` and `advancement_cascade:advancement_loss`

These can be use as such:
```
├ type: one of "only", "any", "any_of" and "some_of".
├ target: if the type isn't "any", an advancement target or an array of advancement targets upon which to trigger the criterion.
| ├ id: required, the ID of the targeted advancement. The target can be passed as only the ID string rather than an object.
| └ required: whether the advancement must be found to be able to validate the criterion. Optional, true by default.
├ min: if the type is "some_of", indicates how many of the target array should be obtained to validate the criterion. Defaults to 1.
└ predicate: a Player predicate, based off of loot conditions, to be validated to trigger the criterion.
```

Please refer to the wiki's [advancement](https://minecraft.wiki/w/Advancement/JSON_format) and [predicate](https://minecraft.wiki/w/Predicate) pages for more details on their respective format.