<style>
  .indent {
    margin-left: 25px;
  }
</style>

# RPGL
RPGL (Role-Playing Game Library) is a Java code library designed to automate certain common game mechanics for table-top
role-playing games (TTRPG's), namely Dungeons and Dragons 5th edition. RPGL employs a generic solution to accomplish
this goal, giving clients access to an open-ended system for creating and then using novel game content.

This README file is designed to guide new users in installing and using RPGL to create a new RPGL client program, as
well as developing your own datapack with novel game content.

# How to include RPGL in a Java application
### 1. Using Gradle
If you are starting a new Gradle project, you can add RPGL to your project as a git dependency. To accomplish this, you
can use [GradleGitDependencies](https://github.com/alexvasilkov/GradleGitDependenciesPlugin), developed by Alex Vasilkov.

- In your `settings.gradle` file, add GradleGitDependencies as a plugin as shown below:
    
  ```
  // this 'plugins' section has to be located at the top of the file
  plugins {
    id 'com.alexvasilkov.git-dependencies' version '2.0.4'
  }
  ```

- In your `build.gradle` file, add RPGL as a git dependency as shown below:

  ```
  git {
    implementation 'https://github.com/CalvinTheRed/RPGL.git', { tag 'v0.17.0-alpha' }
  }
  ```
  Note that the `tag` shown above may be different depending on which version of RPGL you wish to use. You may provide
  any tag present in the RPGL git [repository](https://github.com/CalvinTheRed/RPGL/releases) (though the latest release
  is always recommended).

- Next, load Gradle changes. You should see RPGL included in your project's `libs` directory, and you should now be able
  to reference RPGL data types in your client code.

# Supported Java versions
RPGL requires Java version 15 or higher.

# Help
If you experience difficulties using RPGL, or if any features are not detailed in this doc, you can contact the
developer at calvin@brianandkathi.com, or you can [raise an issue](https://github.com/CalvinTheRed/RPGL/issues) on the
RPGL repository.

# RPGL Data Types
RPGL makes use of a number of custom classes which clients must understand to effectively use it. This section is
intended to give a high-level summary of each of the core data types introduced by RPGL.

- **RPGLEffect.** This class represents anything which might influence how RPGL processes certain events. This includes
  typical status effects such as being poisoned or stunned, as well as other more mechanical features such as scoring
  critical hits on rolls of 19 or 20 on the d20. The client is encouraged to use the `RPGLFactory.newEffect(...)` method
  to create objects of this type.
- **RPGLEvent.** This class represents any verb which occurs within RPGL. This includes the likes of swinging a sword,
  shooting a crossbow, casting a spell, or drinking a potion. The client is encouraged to use the
  `RPGLFactory.newEvent(...)` method to create objects of this type.
- **RPGLItem.** This class represents any physical item or artifact. This includes the likes of swords, potions,
  teacups, and suits of armor. The client is encouraged to use the`RPGLFactory.newItem(...)` method to create objects of
  this type.
- **RPGLObject.** This class represents anything which might appear on a game map or world map. This includes the likes
  of goblins, dragons, chairs, barrels, and unattended items. The client is encouraged to use the
  `RPGLFactory.newObject(...)` method to create objects of this type.
- **RPGLResource.** This class represents any expendable, non-item resources which might be expended by an object during
  a turn. This includes the likes of spell slots, actions, ki points, and action surges. The client is encouraged to use
  the `RPGLFactory.newResource(...)` method to create objects of this type.


- **RPGLClass.** This class represents any role-playing class (as distinct from Java classes). This includes the likes
  of wizards, fighters, and rangers. The client is encouraged to use the `RPGLFactory.getClass(...)` method to get
  references to objects of this type (this data type is not designed to have new instances created by the client, and
  doing so is not recommended).
- **RPGLRace.** This class represents any role-playing race. This includes the likes of humans, elves, and pixies. The
  client is encouraged to use the `RPGLFactory.getRace(...)` method to get references to objects of this type (this data
  type is not designed to have new instances created by the client, and doing so is not recommended).


- **Subevent.** This class represents a particular low-level game mechanic. This includes the likes of making an attack
  roll, making a saving throw, making an ability check, determining an ability score, and gathering damage for a damage
  roll. Not all Subevents are accessible when defining an RPGLEvent, but all RPGLEvents are defined in terms of
  Subevents.
- **Function.** This class represents a particular modification which an RPGLEffect can make to a Subevent. This
  includes the likes of adding 1d6 additional fire damage to a damage collection, adding an effective tag to an
  RPGLObject, and granting advantage to a d20 roll. The changes made by RPGLEffects are defined in terms of Functions.
- **Condition.** This class represents a criterion which must be met in order for an RPGLEffect to execute its Functions
  on a Subevent. This includes the likes of checking an object for a tag, checking if a damage roll includes fire
  damage, and checking whether the source of a Subevent is wielding a particular item. RPGLEffects' criterion for acting
  on a Subevent are defined in terms of Conditions.

# Creating Datapacks
## Versioning
RPGL datapacks are versioned. If your datapack version does not match what is required by your RPGL version, RPGL will
refuse to load it.

_As of RPGL v1.0.0-beta, datapack versioning is not enforced, but a version number must be provided in
order for the datapack to load._

## Datapack Structure
RPGL datapacks have a strict structure, as shown below:

```
datapack/
  classes/
  effects/
  events/
  items/
  objects/
  races/
  resources/
  pack.info
```

Note that `datapack` can be any valid directory name, and indicates the name of the datapack.

Also note that only the `pack.info` file is strictly required for RPGL to load a datapack. Any or all of the directories
may be left out, and any additional files or directories will be ignored by RPGL.

### pack.info
The `pack.info` file contains JSON data, and must have the following format:

```
{
    "version": "Your datapack version number",
    "description": "A totally awesome description of your datapack!"
}
```

## RPGLEffect templates

```
{
  "metadata": {...}
  "name": "Effect Name",
  "description": "Effect Description",
  "subevent_filters": {
    "<subevent id>": [
      {
        "conditions": [
          { <condition instructions> }
        ],
        "functions": [
          { <function instructions> }
        ]
      }
    ]
  }
}
```

<details>
<summary>Read more</summary>

`metadata` can contain any data the datapack developer wishes to include - this is an excellent place to claim ownership
of your content with an author key!

`name` is the name of this Effect, as it would be displayed to a user.

`description` is the description of the Effect, as it would be displayed to a user.

`subevent_filters` is an array of filters defining which subevents this Effect will apply itself to, under what
conditions the Effect will be applied, and what the Effect's effect is when applied.

`subevent_filters.<subevent id>` is a set of behaviors which apply to particular types of subevents being invoked within
RPGL.

`subevent_filters.<subevent id>[#].conditions` is an array of condition instructions which must be satisfied in order
for the Effect to run its Functions.

`subevent_filters.<subevent id>[#].functions` is an array of function instructions which constitute the effect this
Effect has on qualifying Subevents.

_As a rule of thumb, every Effect should have an `"objects_match"` Condition for each behavior to ensure that the
Effect is restricted to only affect Subevents originating from or directed to the intended subject._

<div class="indent">
  <details>
  <summary>See example: Fighting Style (Archery)</summary>

  **Fighting Style (Archery)**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Fighting Style (Archery)",
    "description": "You gain a +2 bonus to ranged attack rolls.",
    "subevent_filters": {
      "attack_roll": [
        {
          "conditions": [
            {
              "condition": "objects_match",
              "subevent": "source",
              "effect": "target"
            },
            {
              "condition": "subevent_has_tag",
              "tag": "ranged"
            }
          ],
          "functions": [
            {
              "function": "add_bonus",
              "bonus_formula": "range",
              "dice": [ ],
              "bonus": 2
            }
          ]
        }
      ]
    }
  }
  ```
  </details>
  <details>
  <summary>See example: Fire Immunity</summary>

  **Fire Immunity**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Fire Immunity",
    "description": "Creatures with this effect take 0 fire damage.",
    "subevent_filters": {
      "damage_affinity": [
        {
          "conditions": [
            {
              "condition": "objects_match",
              "effect": "target",
              "subevent": "target"
            }
          ],
          "functions": [
            {
              "function": "grant_immunity",
              "damage_type": "fire"
            }
          ]
        }
      ]
    }
  }
  ```
  </details>
  <details>
  <summary>See example: Skill Proficiency (Acrobatics)</summary>

  **Skill Proficiency (Acrobatics)**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Skill Proficiency (Acrobatics)",
    "description": "You have proficiency in skill checks made using Acrobatics.",
    "subevent_filters": {
      "ability_check": [
        {
          "conditions": [
            {
              "condition": "objects_match",
              "effect": "target",
              "subevent": "source"
            },
            {
              "condition": "check_skill",
              "skill": "acrobatics"
            }
          ],
          "functions": [
            {
              "function": "give_proficiency"
            }
          ]
        }
      ]
    }
  }
  ```
  </details>
</div>
  
</details>

## RPGLEvent templates

```
{
  "metadata": {...}
  "name": "Event Name",
  "description": "Event Description",
  "area_of_effect": {...},
  "cost": [
    {
      "resource_tags": [...],
      "count": #,
      "minimum_potency": #,
      "scale": [
        {
          "field": "path.to.field",
          "magnitude": #
        }
      ]
    }
  ],
  "subevents": [
    { <subevent instructions> }
  ]
}
```

<details>
<summary>Read more</summary>

`metadata` can contain any data the datapack developer wishes to include - this is an excellent place to claim ownership
of your content with an author key!

`name` is the name of this Event, as it would be displayed to a user.

`description` is the description of the Event, as it would be displayed to a user.

`area_of_effect` indicates the area of effect of the spell. As of RPGL v1.0.0-beta, this field is not used by RPGL and
is just here for the client program's reference (though it can still be scaled with resource scaling).

`cost` is an array of resource costs which must be provided in order to invoke the Event.

`cost[#].resource_tags` is an array of resource tags, at least one of which must be possessed by a resource to satisfy
this resource cost.

`cost[#].count` is the number of the specified resources which are required to satisfy this resource cost. This field
defaults to 1 if not specified.

`cost[#].minimum_potency` is the minimum potency which a resource must possess to satisfy this resource cost. This field
defaults to 1 if not specified.

`cost[#].scale` indicates how the Event changes when you provide it with a resource whose minimum potency exceeds what
is required. This field defaults to `[]` if not specified.

`cost[#].scale[#].field` points to which field in the Event's JSON data will be modified when the resource's potency
exceeds what is required.

`cost[#].scale[#].magnitude` is the amount by which _field_ is increased per excess potency when the resource's potency
exceeds what is required.

_Note that_ count _and_ minimum_potency _will default to 1 if not specified, and that_ scale _will default to `[]` if
not specified._

`subevents` indicates which subevents are invoked when this Event is invoked.

<div class="indent">
  <details>
  <summary>See example: Fireball</summary>

  **Fireball**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Fireball",
    "description": "Hurl a mighty fireball at your foes!",
    "area_of_effect": {
  
    },
    "cost": [
      {
        "resource_tags": [ "action" ]
      },
      {
        "resource_tags": [ "spell_slot" ],
        "count": 1,
        "minimum_potency": 3,
        "scale": [
          {
            "field": "subevents[0].damage[0].dice[0].count",
            "magnitude": 1
          }
        ]
      }
    ],
    "subevents": [
      {
        "subevent": "saving_throw",
        "tags": [ "spell" ],
        "difficulty_class_ability": "int",
        "save_ability": "dex",
        "damage": [
          {
            "damage_formula": "range",
            "damage_type": "fire",
            "dice": [
              { "count": 8, "size": 6 }
            ],
            "bonus": 0
          }
        ],
        "damage_on_pass": "half"
      }
    ]
  }
  ```
  </details>
  <details>
  <summary>See example: Second Wind</summary>

  **Second Wind**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Second Wind",
    "description": "Recover a small amount of hit points quickly in combat.",
    "area_of_effect": { },
    "cost": [
      {
        "resource_tags": [ "bonus_action" ]
      },
      {
        "resource_tags": [ "second_wind_charge" ]
      }
    ],
    "subevents": [
      {
        "subevent": "heal",
        "tags": [ "second_wind" ],
        "healing": [
          {
            "healing_formula": "range",
            "dice": [
              { "count": 1, "size": 10 }
            ],
            "bonus": 0
          },
          {
            "healing_formula": "level",
            "class": "std:fighter",
            "object": {
              "from": "subevent",
              "object": "source"
            }
          }
        ]
      }
    ]
  }
  ```
  </details>
  <details>
  <summary>See example: Fire Breath</summary>

  **Fire Breath**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Fire Breath",
    "description": "The dragon breathes fire.",
    "area_of_effect": {
  
    },
    "cost": [
      {
        "resource_tags": [ "action" ]
      },
      {
        "resource_tags": [ "breath_attack" ]
      }
    ],
    "subevents": [
      {
        "subevent": "saving_throw",
        "difficulty_class_ability": "con",
        "save_ability": "dex",
        "damage": [
          {
            "damage_formula": "range",
            "damage_type": "fire",
            "dice": [
              { "count": 16, "size": 6 }
            ],
            "bonus": 0
          }
        ],
        "damage_on_pass": "half"
      }
    ]
  }
  ```
  </details>
</div>

</details>

## RPGLItem templates

```
{
  "metadata": {...}
  "name": "Item Name",
  "description": "Item Description",
  "tags": [...],
  "weight": #,
  "cost": #,
  "events": {
    "one_hand": [...],
    "multiple_hands": [...],
    "special": [...]
  },
  "equipped_effects": [...],
  "equipped_resources": [...],
  
  "attack_bonus": #,
  "damage_bonus": #,
  
  "armor_class_base": #,
  "armor_class_dex_limit": #,
  
  "armor_class_bonus": #
}
```

<details>
<summary>Read more</summary>

`metadata` can contain any data the datapack developer wishes to include - this is an excellent place to claim ownership
of your content with an author key!

`name` is the name of this Item, as it would be displayed to a user.

`description` is the description of the Item, as it would be displayed to a user.

`tags` is a list of tags which describe the Item. This field defaults to `[]` if not specified.

`weight` is the weight of the Item. This field defaults to 0 if not specified.

`cost` is the cost of the Item. This field defaults to 0 if not specified.

`events` indicates which events the Item can grant to an Object. This field defaults to `{}` if not specified.

`events.one_hand` is a list of events which are granted to the wielder of this Item when they hold it with one hand.
This field defaults to `[]` if not specified.

`events.multiple_hands` is a list of events which are granted to the wielder of this Item when they hold it with
multiple hands. This field defaults to `[]` if not specified.

`events.special` is a list of events which are granted to the wielder of this Item when they have it equipped in any
inventory slot. This field defaults to `[]` if not specified.

`equipped_effects` is a list of Effects which are applied to an Object when the Item is equipped. This field defaults to
`[]` if not specified.

`equipped_resources` is a list of Resources which are granted to an Object when the Item is equipped. This field
defaults to `[]` if not specified.

`attack_bonus` is a bonus applied to any attack rolls made using this Item as a weapon. This field may be ignored if the
Item is not a weapon.

`damage_bonus` is a bonus applied to any damage rolls made using this item as a weapon. This field may be ignored if the
Item is not a weapon.

`armor_class_base` is the base value an Object's armor class will be set to when wearing this Item as armor. This field
may be ignored if this Item is not armor.

`armor_class_dex_limit` is the maximum bonus an Object can receive to its armor class from its Dexterity modifier when
wearing this Item as armor. This field may be ignored if this Item is not armor **OR** if this Item does not restrict
this bonus.

`armor_class_bonus` is the bonus provided to an Object's armor class when the Item is being wielded as a shield. This
field may be ignored if this Item is not a shield.

<div class="indent">
  <details>
  <summary>See example: Breastplate</summary>

  **Breastplate**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Breastplate",
    "description": "A suit of breastplate armor.",
    "tags": [
      "metal",
      "armor",
      "medium_armor"
    ],
    "weight": 20,
    "cost": 400,
    "armor_class_base": 14,
    "armor_class_dex_limit": 2
  }
  ```
  </details>
  <details>
  <summary>See example: Greatsword</summary>

  **Greatsword**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Greatsword",
    "description": "A greatsword.",
    "tags": [
      "greatsword",
      "heavy",
      "metal",
      "martial_melee",
      "two_handed",
      "weapon"
    ],
    "weight": 6,
    "cost": 50,
    "events": {
      "multiple_hands": [
        "std:item/weapon/melee/martial/greatsword/melee",
        "std:common/improvised_thrown"
      ],
      "one_hand": [
        "std:common/improvised_melee",
        "std:common/improvised_thrown"
      ],
      "special": [ ]
    },
    "equipped_effects": [ ],
    "attack_bonus": 0,
    "damage_bonus": 0
  }
  ```
  </details>
  <details>
  <summary>See example: Potion of Healing</summary>

  **Potion of Healing**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Potion of Healing",
    "description": "This potion heals a creature for 2d4+2 hit points when consumed.",
    "tags": [
      "potion_of_healing"
    ],
    "weight": 1,
    "cost": 50,
    "events": {
      "multiple_hands": [
        "std:item/potion/potion_of_healing/drink",
        "std:common/improvised_melee",
        "std:common/improvised_thrown"
      ],
      "one_hand": [
        "std:item/potion/potion_of_healing/drink",
        "std:common/improvised_melee",
        "std:common/improvised_thrown"
      ],
      "special": [ ]
    }
  }
  ```
  </details>
</div>

</details>

## RPGLObject templates

```
{
  "metadata": {...}
  "name": "Object Name",
  "description": "Object Description",
  "tags": [...],
  "ability_scores": {...},
  "health_data": {
    "base": #,
    "current": #,
    "temporary": #
  },
  "classes": [
    {
      "id": "class ID",
      "level": #,
      "choices": {
        <choice name>: [...]
      }
    }
  ],
  "races": [...],
  "equipped_items": {...},
  "inventory": [...],
  "events": [...],
  "effects": [...],
  "resources": [...],
  "challenge_rating": #,
  "proficiency_bonus": #
}
```

<details>
<summary>Read more</summary>

`metadata` can contain any data the datapack developer wishes to include - this is an excellent place to claim ownership
of your content with an author key!

`name` is the name of this Object, as it would be displayed to a user.

`description` is the description of the Object, as it would be displayed to a user.

`tags` is a list of tags which describe the Object. This field defaults to `[]` if not specified.

`ability_scores` is an object containing the raw ability scores for the Object.

`health_data` contains information about the Object's health.

`health_data.base` indicates the Object's base hit points (not including any modifiers provided by ability scores, level
gain, and other features).

`health_data.current` indicates the Object's current hit points.

`health_data.temporary` indicates how many temporary hit points the Object has.

`classes` is an array of the Object's levels in various Classes. This field defaults to `[]` if not specified.

`classes[#].id` is a particular Class's ID.

`classes[#].level` is how many levels the Object has in a Class.

`classes[#].choices` indicates what choices the Object makes whenever a new level requires a choice be made, such as a
Fighter choosing a Fighting Style at level 1. This field defaults to `{}` if not specified.

`classes[#].choices.<choice name>` is an array of indices for selected options offered by a particular choice. These
indices will correspond to the order of the options as defined in the appropriate Class template.

`races` is an array of Race IDs indicating which Race(s) the Object belongs to. If the Object has a sub-race, that can
be treated as a second Race and included in addition to the base Race. This field defaults to `[]` if not specified.

_Note that as of RPGL v1.0.0-beta, if an Object has a Race specified in its template, any choices which must be made
during the Object's creation must be included in the_ choices _fields of the Object's_ classes.

`equipped_items` is an object indicating what items the Object has in which equipment slot. This field defaults to `{}`
if not specified.

`inventory` is an array of Items carried by the Object, beyond what it already has equipped. This field defaults to `[]`
if not specified.

`events` is an array indicating any extra Events made available to the Object. This field defaults to `[]` if not
specified.

`effects` is an array indicating any extra Effects applied to the Object. This field defaults to `[]` if not specified.

`resources` is an array indicating any extra Resources made available to the Object. This field defaults to `[]` if not
specified.

`challenge_rating` indicates the challenge rating of the Object, if it has one. Note that this field contains a double,
allowing for decimal values. This field is optional and will default to `null` if not specified.

`proficiency_bonus` indicates the Object's proficiency bonus. If not specified, this field will default to `null` and
any references to the Object's proficiency bonus will determine its value according to the Object's level.

<div class="indent">
  <details>
  <summary>See example: Knight</summary>

  **Knight**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Knight",
    "description": "A knight.",
    "tags": [
      "humanoid"
    ],
    "ability_scores": {
      "str": 16,
      "dex": 11,
      "con": 14,
      "int": 11,
      "wis": 11,
      "cha": 15
    },
    "health_data": {
      "base": 36,
      "current": 52,
      "temporary": 0
    },
    "equipped_items": {
      "mainhand": "std:weapon/melee/martial/longsword",
      "offhand": "std:armor/shield/metal",
      "armor": "std:armor/heavy/plate"
    },
    "inventory": [
      "std:weapon/ranged/martial/heavy_crossbow"
    ],
    "classes": [
      {
        "id": "std:common/base",
        "level": 1
      },
      {
        "id": "std:common/hit_die/d8",
        "level": 8
      }
    ],
    "races": [
      "std:human"
    ],
    "proficiency_bonus": 2,
    "challenge_rating": 3
  }
  ```
  </details>
  <details>
  <summary>See example: Commoner</summary>

  **Commoner**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Commoner",
    "description": "A Commoner.",
    "ability_scores": {
      "str": 10,
      "dex": 10,
      "con": 10,
      "int": 10,
      "wis": 10,
      "cha": 10
    },
    "health_data": {
      "base": 2,
      "current": 2,
      "temporary": 0
    },
    "proficiency_bonus": 2,
    "challenge_rating": 0
  }
  ```
  </details>
  <details>
  <summary>See example: Young Red Dragon</summary>

  **Young Red Dragon**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Young Red Dragon",
    "description": "A young red dragon.",
    "tags": [
      "dragon"
    ],
    "ability_scores": {
      "str": 23,
      "dex": 10,
      "con": 21,
      "int": 14,
      "wis": 11,
      "cha": 19
    },
    "health_data": {
      "base": 93,
      "current": 178,
      "temporary": 0
    },
    "classes": [
      {
        "id": "std:monster/dragon/red/young",
        "level": 17
      }
    ],
    "races": [
      "std:dragon/red"
    ],
    "proficiency_bonus": 4,
    "challenge_rating": 10
  }
  ```
  </details>
</div>

</details>

## RPGLResource templates

```
{
  "metadata": {...}
  "name": "Resource Name",
  "description": "Resource Description",
  "tags": [...],
  "potency": #,
  "refresh_criterion": [
    {
      "subevent": "<subevent id>",
      "tags": [...],
      "actor": "source" | "target" | "any",
      "chance": #,
      "required_generator": {
        "dice": [
          {
            "count": #,
            "size": #
          }
        ],
        "bonus": #
      }
    }
  ]
}
```

<details>
<summary>Read more</summary>

`metadata` can contain any data the datapack developer wishes to include - this is an excellent place to claim ownership
of your content with an author key!

`name` is the name of this Resource, as it would be displayed to a user.

`description` is the description of the Resource, as it would be displayed to a user.

`tags` is a list of tags which describe the Resource.

`potency` indicates the potency of the Resource. This field defaults to `1` if not specified.

`refresh_criterion` is an array defining what must occur in order for the Resource to change from being exhausted to not
being exhausted. If any one of the criterion are met, the Resource ceases to be exhausted. This field will default to
`[ { "subevent": "info_subevent", "tags": [ "start_turn" ] } ]` if not specified **OR** if left as an empty array.

`refresh_criterion[#].subevent` is a Subevent ID which can contribute to refreshing the Resource if it is exhausted.

`refresh_criterion[#].tags` is an array of tags which must all be present in the Subevent in order for it to qualify for
refreshing the Resource.

`refresh_criterion[#].actor` indicated whether the owner of the Resource must be the source of the Subevent, the target
of the Subevent, or either in order for the Subevent to qualify for refreshing the Resource. This field will default to
`source` if not specified.

`refresh_criterion[#].chance` indicates the odds that the Resource will be refreshed, should the Subevent meet all other
criteria to do so. This should be a value between `1` and `100`. This field will default to `100` if not specified.

`refresh_criterion[#].required_generator` is an object defining the algorithm used to decide how many times the Resource
must be refreshed before it ceases to be exhausted. This algorithm is consulted each time the Resource is exhausted.
This field defaults to `{ "dice": [], "bonus": 1 }` if not specified. 

`refresh_criterion[#].required_generator.dice` indicates any dice which must be rolled as a part of determining how many
times the Resource must be refreshed.

`refresh_criterion[#].required_generator.dice[#].count` is the number of dice to roll.

`refresh_criterion[#].required_generator.dice[#].size` is the size of dice being rolled.

`refresh_criterion[#].required_generator.bonus` indicates any static bonus added to the number of times the Resource
must be refreshed

<div class="indent">
  <details>
  <summary>See example: Action</summary>

  **Action**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Action",
    "description": "This resource allows you to take actions on your turn.",
    "tags": [
      "action"
    ]
  }
  ```
  </details>
  <details>
  <summary>See example: 4th-Level Warlock Spell Slot</summary>

  **4th-Level Warlock Spell Slot**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Spell Slot (Pact Magic)",
    "description": "This resource allows you to cast spells and use certain Invocations.",
    "tags": [
      "spell_slot",
      "pact_spell_slot"
    ],
    "potency": 4,
    "refresh_criterion": [
      {
        "subevent": "info_subevent",
        "tags": [
          "short_rest"
        ]
      },
      {
        "subevent": "info_subevent",
        "tags": [
          "long_rest"
        ]
      }
    ]
  }
  ```
  </details>
  <details>
  <summary>See example: d10 Hit Die</summary>

  **d10 Hit Die**

  ```
  {
    "metadata": {
      "author": "Calvin Withun"
    },
    "name": "Hit Die (1d10)",
    "description": "This resource represents your body's ability to heal itself, and can be used to heal during rests.",
    "tags": [
      "hit_die"
    ],
    "potency": 10
  }
  ```
  </details>
</div>

</details>

# Subevents
RPGL makes use of many different Subevents, all with their own unique purpose and a unique set of Conditions and
Functions which they can synergize with. Fortunately for new datapack developers, not every Subevent is
Event-compatible! Many of them operate in the background, and will never appear in an Event template. This section will
provide an overview for all Subevents, and indicate which ones can be directly used in an Event.

_Note that which Conditions and Functions synergize with which Subevents will be described in the_ Conditions _and_
Functions _sections following this one._

<details>
<summary>AbilityCheck</summary>

**AbilityCheck**

```
{
  "subevent": "ability_check",
  "tags": [...],
  "ability": "...",
  "skill": "..."
}
```

This subevent is dedicated to resolving ability checks, including skill checks and ability checks involving tools.

Source: an RPGLObject initiating an ability check

Target: an RPGLObject being required to make an ability check

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.

  `tags` is an array of tags which describe the Subevent.

  `ability` is which ability score the subevent source uses to make the check.

  `skill` is which skill is involved in making the check. This field is optional; if it is not specified, it will remain
  null and the ability check will resolve without a skill.

  _Note that_ skill _can also be used to indicate a tool._

  Conditions:

  <ul>
    <li>CheckAbility</li>
    <li>CheckSkill</li>
    <li>EquippedItemHasTag</li>
    <li>SubeventHasTag</li>
  </ul>

  Functions:

  <ul>
    <li>AddBonus</li>
    <li>CancelSubevent</li>
    <li>GiveExpertise</li>
    <li>GiveHalfProficiency</li>
    <li>GiveProficiency</li>
    <li>GrantAdvantage</li>
    <li>GrantDisadvantage</li>
    <li>SetBase</li>
    <li>SetMinimum</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--AbilityCheck-->

<details>
<summary>AbilityContest</summary>

**AbilityContest**

```
{
  "subevent": "ability_contest",
  "tags": [...]
  "source_check": {
    "ability": "...",
    "skill": "..."
  },
  "target_check": {
    "ability": "...",
    "skill": "..."
  },
  "pass": [
    { <subevent instructions> }
  ],
  "fail": [
    { <subevent instructions> }
  ]
}
```

This Subevent is dedicated to resolving ability contests between two RPGLObjects. It creates two `AbilityCheck`
Subevents, one for the source and one for the target, and pits them against each other.

Source: an RPGLObject initiating an ability check

Target: an RPGLObject against whom an ability check is being initiated

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.

  `source_check` indicates instructions for the ability check to be made by the source of the Subevent.

  `source_check.ability` is which ability score the subevent source uses to make the check.

  `source_check.skill` is which skill is involved in making the check. This field is optional; if it is not specified,
  it will remain null and the ability check will resolve without a skill.

  _Note that_ source_check.skill _can also be used to indicate a tool._

  `target_check` indicates instructions for the ability check to be made by the target of the Subevent.
  
  `target_check.ability` is which ability score the subevent target uses to make the check.

  `target_check.skill` is which skill is involved in making the check. This field is optional; if it is not specified,
  it will remain null and the ability check will resolve without a skill.
  
  _Note that_ target_check.skill _can also be used to indicate a tool._
  
  Conditions:

  _This Subevent has no special Conditions with which it is compatible._

  Functions:

  _This Subevent has no special Functions with which it is compatible._

  </details>
  <br/>
</div>
</details><!--AbilityContest-->

<details>
<summary>AbilitySave</summary>

**AbilitySave**

```
{
  "subevent": "ability_save",
  "tags": [...],
  "ability": "...",
  "skill": "...",
  "difficulty_class_ability": "...",
  "pass": [
    { <subevent instructions> }
  ],
  "fail": [
    { <subevent instructions> }
  ]
}
```

This Subevent is dedicated to making an ability save and resolving all fallout from making the save. It
creates an `AbilityCheck` Subevent to perform the ability check used for the save, and uses a
`CalculateSaveDifficultyClass` Subevent to determine the save DC.

Source: an RPGLObject requiring that other RPGLObjects make an ability save

Target: an RPGLObject making an ability save

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `ability` is which ability score the subevent target uses to make the save.
  
  `skill` is which skill is involved in making the save. This field is optional; if it is not specified, it will remain
  null and the ability save will resolve without a skill.
  
  _Note that_ skill _can also be used to indicate a tool._
  
  `difficulty_class_ability` is the ability score the Subevent source uses to determine the difficulty class of the save.
  
  `pass` is an array of Subevent instructions establishing the fallout when the save passes. The save is considered to
  have passed if the save meets or exceeds its difficulty class.
  
  `fail` is an array of Subevent instructions establishing the fallout when the save fails. The save is considered to have
  failed if the save is less than its difficulty class.
  
  Conditions:

  _This Subevent has no special Conditions with which it is compatible._

  Functions:

  <ul>
    <li>CancelSubevent</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--AbilitySave-->

<details>
<summary>AddOriginItemTag</summary>

**AddOriginItemTag**

```
{
  "subevent": "add_origin_item_tag",
  "tags": [...],
  "tag": "..."
}
```

This Subevent is dedicated to adding a tag to an item (specifically the origin item of an Event).

Source: an RPGLObject adding a tag to an origin item

Target: should be the same as the source

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `tag` is the tag to be added to the origin item.
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:

  _This Subevent has no special Functions with which it is compatible._

  </details>
  <br/>
</div>
</details><!--AddOriginItemTag-->

<details>
<summary>AttackAbilityCollection</summary>

**AddOriginItemTag**

This Subevent is dedicated to collecting non-standard attack abilities for attacks made with an Item.

Source: an RPGLObject collecting valid events for weapon attacks

Target: should be the same as the source

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddAttackAbility</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--AttackAbilityCollection-->

<details>
<summary>AttackRoll</summary>

**AttackRoll**

```
{
  "subevent": "attack_roll",
  "tags": [...],
  "attack_type": "melee" | "ranged" | "thrown",
  "attack_ability": "...",
  "damage": [
    { <damage instructions> }
  ],
  "withhold_damage_modifier": t/f,
  "vampirism": {
    "damage_type": "...",
    "round_up": t/f,
    "numerator": #,
    "denominator": #
  }
}
```

This Subevent is dedicated to making an attack roll and resolving all fallout from making the attack.

Source: an RPGLObject adding a tag to an origin item

Target: should be the same as the source

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `attack_type` is the type of attack this Subevent represents.

  `attack_ability` indicates which ability is used for the attack.

  `damage` is an array of damage instructions defining the damage dealt by the attack on a hit.

  `withhold_damage_modifier` indicates whether the attack's damage should include the modifier from the attack ability.

  `vampirism` indicates any vampiric properties possessed by the attack. This allows the attacker to heal some portion
  of the damage dealt by the attack. This field is optional, and if left unspecified, the attack will resolve without
  applying any vampirism.

  `vampirism.damage_type` indicates which damage type has the vampiric property. This field is optional, and all damage
  dealt by the attack will become vampiric if not specified.

  `vampirism.round_up` indicates whether the vampiric calculation should round up. This field defaults to `false` if not
  specified.

  `vampirism.numerator` is the numerator by which the vampiric damage is multiplied during calculation to determine the
  vampiric healing. This field defaults to `1` if not specified.

  `vampirism.denominator` is the denominator by which the vampiric damage is divided during calculation to determine the
  vampiric healing. This field defaults to `2` if not specified.
  
  Conditions:
  
  <ul>
    <li>CheckAbility</li>
  </ul>
  
  Functions:
  
  <ul>
    <li>ApplyVampirism</li>
    <li>CancelSubevent</li>
    <li>GrantAdvantage</li>
    <li>GrantDisadvantage</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--AttackRoll-->

<details>
<summary>CalculateAbilityScore</summary>

**CalculateAbilityScore**

This Subevent is dedicated to calculating an ability score.

Source: the RPGLObject whose ability score is being calculated

Target: should be the same as the source

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddBonus</li>
    <li>SetBase</li>
    <li>SetMinimum</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--CalculateAbilityScore-->

<details>
<summary>CalculateBaseArmorClass</summary>

**CalculateBaseArmorClass**

This subevent is dedicated to calculating the armor class against which attack rolls are made. Once the attack roll is
made, the target will have an opportunity to raise its armor class further through the `CalculateEffectiveArmorClass`
subevent, but reactive changes to armor class are not accounted for in this subevent.

Source: the RPGLObject whose base armor class is being calculated

Target: should be the same as the source

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddBonus</li>
    <li>SetBase</li>
    <li>SetMinimum</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--CalculateBaseArmorClass-->

<details>
<summary>CalculateCriticalHitThreshold</summary>

**CalculateCriticalHitThreshold**

This subevent is dedicated to calculating the threshold which must be met on the d20 of an attack to count as a critical
hit. Typically, an attack is a critical hit if the d20 rolls a 20.

Source: the RPGLObject whose critical hit threshold is being calculated

Target: should be the same as the source

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddBonus</li>
    <li>SetBase</li>
    <li>SetMinimum</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--CalculateCriticalHitThreshold-->

<details>
<summary>CalculateEffectiveArmorClass</summary>

**CalculateEffectiveArmorClass**

This subevent is dedicated to calculating the armor class against which attack rolls are made for the purposes of
determining whether an attack hits or misses. This value accounts for reactive increases in armor class made after the
attack roll is determined.

Source: the RPGLObject whose effective armor class is being calculated

Target: should be the same as the source

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddBonus</li>
    <li>SetBase</li>
    <li>SetMinimum</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--CalculateEffectiveArmorClass-->

<details>
<summary>CalculateMaximumHitPoints</summary>

**CalculateMaximumHitPoints**

This Subevent is dedicated to calculating the maximum hit points of an RPGLObject.

Source: the RPGLObject whose maximum hit point value is being calculated

Target: should be the same as the source

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddBonus</li>
    <li>SetBase</li>
    <li>SetMinimum</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--CalculateMaximumHitPoints-->

<details>
<summary>CalculateProficiencyBonus</summary>

**CalculateProficiencyBonus**

This subevent is dedicated to calculating the proficiency bonus of an RPGLObject.

Source: the RPGLObject whose proficiency bonus is being calculated

Target: should be the same as the source

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddBonus</li>
    <li>SetBase</li>
    <li>SetMinimum</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--CalculateProficiencyBonus-->

<details>
<summary>CalculateSaveDifficultyClass</summary>

**CalculateSaveDifficultyClass**

This subevent is dedicated to calculating the save difficulty class against which saving throws are made.

Source: the RPGLObject whose save difficulty class is being calculated

Target: should be the same as the source

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddBonus</li>
    <li>SetBase</li>
    <li>SetMinimum</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--CalculateSaveDifficultyClass-->

<details>
<summary>CriticalDamageCollection</summary>

**CriticalDamageCollection**

This Subevent is dedicated to representing a collection of damage to be rolled when a critical hit occurs.

Source: an RPGLObject delivering a critical hit attack

Target: an RPGLObject targeted by a critical hit attack

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  <ul>
    <li>IncludesDamageType</li>
  </ul>
  
  Functions:
  
  <ul>
    <li>AddDamage</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--CriticalDamageCollection-->

<details>
<summary>CriticalDamageConfirmation</summary>

**CriticalDamageConfirmation**

This Subevent is dedicated to confirming that a critical hit deals critical damage.

Source: an RPGLObject scoring a critical hit

Target: an RPGLObject suffering a critical hit

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>CancelSubevent</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--CriticalDamageConfirmation-->

<details>
<summary>DamageAffinity</summary>

**DamageAffinity**

This Subevent is dedicated to determining the affinity an RPGLObject has for a particular damage type - normal, immune,
resistant, or vulnerable.

Source: an RPGLObject attempting to deal the indicated damage type

Target: an RPGLObject being targeted by typed damage

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  <ul>
    <li>IncludesDamageType</li>
  </ul>
  
  Functions:
  
  <ul>
    <li>GrantImmunity</li>
    <li>GrantResistance</li>
    <li>GrantVulnerability</li>
    <li>RevokeImmunity</li>
    <li>RevokeResistance</li>
    <li>RevokeVulnerability</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--DamageAffinity-->

<details>
<summary>DamageCollection</summary>

**DamageCollection**

This Subevent is dedicated to collecting unrolled damage dice and bonuses.

Source: an RPGLObject preparing to deal damage

Target: an RPGLObject which will later suffer the collected damage

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  <ul>
    <li>IncludesDamageType</li>
  </ul>
  
  Functions:
  
  <ul>
    <li>AddDamage</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--DamageCollection-->

<details>
<summary>DamageDelivery</summary>

**DamageDelivery**

This Subevent is dedicated to delivering a quantity of typed damage to an RPGLObject.

Source: an RPGLObject dealing damage

Target: an RPGLObject suffering damage

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  <ul>
    <li>IncludesDamageType</li>
  </ul>
  
  Functions:
  
  <ul>
    <li>MaximizeDamage</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--DamageDelivery-->

<details>
<summary>DamageRoll</summary>

**DamageRoll**

This Subevent is dedicated to rolling damage dice.

Source: an RPGLObject rolling damage

Target: an RPGLObject which will later suffer the rolled damage

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  <ul>
    <li>IncludesDamageType</li>
  </ul>
  
  Functions:
  
  <ul>
    <li>MaximizeDamage</li>
    <li>RerollDamageDiceMatchingOrBelow</li>
    <li>SetDamageDiceMatchingOrBelow</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--DamageRoll-->

<details>
<summary>DealDamage</summary>

**DealDamage**

```
{
  "subevent": "deal_damage",
  "tags": [...],
  "damage": [
    { <damage instructions> }
  ],
  "vampirism": {
    "damage_type": "...",
    "round_up": t/f,
    "numerator": #,
    "denominator": #
  }
}
```

This Subevent is dedicated to directly dealing damage to an RPGLObject without first requiring an attack roll or saving
throw.

Source: an RPGLObject dealing damage

Target: an RPGLObject suffering damage

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.

  `damage` is an array of damage instructions defining the damage dealt to the target.

  `vampirism` indicates any vampiric properties possessed by the Subevent. This allows the attacker to heal some portion
  of the damage dealt by the Subevent. This field is optional, and if left unspecified, the Subevent will resolve
  without applying any vampirism.
  
  `vampirism.damage_type` indicates which damage type has the vampiric property. This field is optional, and all damage
  dealt by the Subevent will become vampiric if not specified.
  
  `vampirism.round_up` indicates whether the vampiric calculation should round up. This field defaults to `false` if not
  specified.
  
  `vampirism.numerator` is the numerator by which the vampiric damage is multiplied during calculation to determine the
  vampiric healing. This field defaults to `1` if not specified.
  
  `vampirism.denominator` is the denominator by which the vampiric damage is divided during calculation to determine the
  vampiric healing. This field defaults to `2` if not specified.
  
  Conditions:
  
  <ul>
    <li>IncludesDamageType</li>
  </ul>
  
  Functions:
  
  <ul>
    <li>ApplyVampirism</li>
    <li>CancelSubevent</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--DealDamage-->

<details>
<summary>DestroyOriginItem</summary>

**DestroyOriginItem**

```
{
  "subevent": "destroy_origin_item",
  "tags": [...]
}
```

This Subevent is dedicated to destroying an Event's origin item, if one exists. This is meant to be used to destroy
consumable items such as potions, or in the case that an Event breaks its origin item.

Source: an RPGLObject destroying an origin item

Target: should be the same as the source

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.

  `tags` is an array of tags which describe the Subevent.

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  _This Subevent has no special Functions with which it is compatible._

  </details>
  <br/>
</div>
</details><!--DestroyOriginItem-->

<details>
<summary>ExhaustResource</summary>

**ExhaustResource**

```
{
  "subevent": "exhaust_resource",
  "tags": [...],
  "resource": "...",
  "count": #,
  "minimum_potency": #,
  "maximum_potency": #,
  "selection_mode": "low_first" | "high_first" | "random"
}
```

This Subevent is dedicated to exhausting a number of RPGLResources according to their Resource ID and potency. This
Subevent allows for prioritization by high, low, or random potency, as well as bounding the potencies which can be
exhausted.

Source: a RPGLObject causing for resources to be exhausted

Target: a RPGLObject whose resources are being exhausted

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `resource` is the resource ID indicating which resource should be exhausted.

  _Note that as of RPGL v1.0.0-beta, there is no way to use this Subevent to exhaust a resource by tags._

  `count` is the number of qualifying resources which should be exhausted by the Subevent. This field defaults to `1` if
  not specified.

  `minimum_potency` is the minimum potency a Resource must have to qualify to be exhausted by this Subevent. This field
  defaults to `0` if not specified.

  `maximum_potency` is the maximum potency a Resource can have to qualify to be exhausted by this Subevent. This field
  defaults to `2147483647` if not specified.

  `selection_mode` indicates the prioritization of qualifying resources by potency. Qualifying resources can be
  exhausted in random order, in order of highest potency to lowest potency, or in order of lowest potency to highest
  potency. This field defaults to `"low_first"` if not specified.
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  _This Subevent has no special Functions with which it is compatible._

  </details>
  <br/>
</div>
</details><!--ExhaustResource-->

<details>
<summary>GetEvents</summary>

**GetEvents**

This subevent is dedicated to gathering a collection of additional RPGLEvent datapack IDs to which the subevent's target
is meant to have access. This allows for RPGLEffects to grant situational access to an RPGLEvent which target might not
otherwise have access to.

Source: an RPGLObject whose events are being listed

Target: should be the same as the source

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddEvent</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--GetEvents-->

<details>
<summary>GetObjectTags</summary>

**GetObjectTags**

This Subevent is dedicated to collecting all tags which apply to a RPGLObject, beyond what appears on its template.

Source: an RPGLObject being queried for tags

Target: should be the same as the source

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddObjectTag</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--GetObjectTags-->

<details>
<summary>GiveEffect</summary>

**GiveEffect**

```
{
  "subevent": "give_effect",
  "tags": [...],
  "effect": "..."
}
```

This Subevent is dedicated to assigning an RPGLEffect to an RPGLObject.

Source: an RPGLObject attempting to apply an RPGLEffect to another RPGLObject

Target: an RPGLObject to whom an RPGLEffect is being applied

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `effect` the Effect ID of the Effect being applied to the target.

  _Note that any Effect applied with this Subevent is given the_ `"temporary"` _tag._
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>CancelSubevent</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--GiveEffect-->

<details>
<summary>GiveResource</summary>

**GiveResource**

```
{
  "subevent": "give_resource",
  "tags": [...],
  "resource": "...",
  "count": #
}
```

This Subevent is dedicated to giving a new RPGLResource to a RPGLObject.

Source: a RPGLObject granting a new resource

Target: a RPGLObject receiving a new resource

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `resource` is the Resource ID for the Resource being given to the target.

  `count` is the number of Resources to be given to the target. This field defaults to `1` if not specified.
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  _This Subevent has no special Functions with which it is compatible._

  </details>
  <br/>
</div>
</details><!--GiveResource-->

<details>
<summary>GiveTemporaryHitPoints</summary>

**GiveTemporaryHitPoints**

```
{
  "subevent": "give_temporary_hit_points",
  "tags": [...],
  "temporary_hit_points": [
    { <temporary hit point instructions> }
  ],
  "rider_effects": [
    "..."
  ]
}
```

This Subevent is dedicated to giving temporary hit points to an RPGLObject. This Subevent also supports granting
RPGLEffects to the Subevent's target if the temporary hit points are successfully assigned to the target.

Source: an RPGLObject granting temporary hit points

Target: an RPGLObject being granted temporary hit points

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `temporary_hit_points` indicates the quantity of temporary hit points to provide the target.

  `rider_effects` is a list of Effect IDs which will be applied to the target so long as the temporary hit points
  provided by this Subevent persist.
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddTemporaryHitPoints</li>
    <li>CancelSubevent</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--GiveTemporaryHitPoints-->

<details>
<summary>Heal</summary>

**Heal**

```
{
  "subevent": "heal",
  "tags": [...],
  "healing": [
    { <healing instructions> }
  ]
}
```

This Subevent is dedicated to performing healing on an RPGLObject.

Source: an RPGLObject performing healing

Target: an RPGLObject being targeted by the healing

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `healing` is an array of healing instructions defining how much healing will be performed by the Subevent.
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>CancelSubevent</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--Heal-->

<details>
<summary>HealingCollection</summary>

**HealingCollection**

This Subevent is dedicated to collecting unrolled healing dice and bonuses.

Source: an RPGLObject preparing to perform healing

Target: an RPGLObject which will later receive the collected healing

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddHealing</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--HealingCollection-->

<details>
<summary>HealingDelivery</summary>

**HealingDelivery**

This Subevent is dedicated to delivering a quantity of healing to an RPGLObject.

Source: an RPGLObject performing healing

Target: an RPGLObject receiving healing

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>MaximizeHealing</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--HealingDelivery-->

<details>
<summary>HealingRoll</summary>

**HealingRoll**

This abstract Subevent is dedicated to rolling healing dice.

Source: an RPGLObject rolling healing

Target: an RPGLObject which will later receive the rolled healing

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>MaximizeHealing</li>
    <li>RerollHealingDiceMatchingOrBelow</li>
    <li>SetHealingDiceMatchingOrBelow</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--HealingRoll-->

<details>
<summary>InfoSubevent</summary>

**InfoSubevent**

```
{
  "subevent": "info_subevent",
  "tags": [...]
}
```

This Subevent is dedicated to communicating the occurrence of some non-functional but informative change, such as
starting a turn, ending a turn, running out of temporary hit points, etc. This type of Subevent allows for a more
flexible degree of responsiveness to be achieved than would be possible through the use of functional Subevents alone.

Source: an RPGLObject emitting information about what it is doing

Target: should be the same as the source

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>CancelSubevent</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--InfoSubevent-->

<details>
<summary>RefreshResource</summary>

**RefreshResource**

```
{
  "subevent": "refresh_resource",
  "tags": [...],
  "resource_tag": "...",
  "count": #,
  "minimum_potency": #,
  "maximum_potency": #,
  "selection_mode": "low_first" | "high_first" | "random"
}
```

This Subevent is dedicated to refreshing a number of RPGLResources according to their Subevent ID and potency. This
Subevent allows for prioritization by high, low, or random potency, as well as bounding the potencies which can be
refreshed.

Source: a RPGLObject causing for resources to be refreshed

Target: a RPGLObject whose resources are being refreshed

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `resource_tag` is a tag which must be possessed by a Resource to be eligible for being refreshed.

  `count` is the number of Resources to be refreshed. This field defaults to `2147483647` if not specified.

  `minimum_potency` is the minimum potency a Resource must have to be eligible for being refreshed. This field defaults
  to `0` if not specified.
  
  `maximum_potency` is the maximum potency a Resource can have while remaining eligible for being refreshed. This field
  defaults to `2147483647` if not specified.

  `selection_mode` indicates the priority by which eligible Resources are refreshed. This Subevent can refresh eligible
  Resources in random order, in order of increasing potency, or in order of decreasing potency.
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  _This Subevent has no special Functions with which it is compatible._

  </details>
  <br/>
</div>
</details><!--RefreshResource-->

<details>
<summary>RemoveEffect</summary>

**RemoveEffect**

```
{
  "subevent": "remove_effect",
  "tags": [...],
  "effect_tags": [
    "..."
  ]
}
```

This Subevent is dedicated to removing RPGLEffects from an RPGLObject.

Source: an RPGLObject attempting to remove RPGLEffects from a RPGLObject

Target: an RPGLObject from whom an RPGLEffect is being removed

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `effect_tags` is an array of tags which must all be possessed by an Effect in order for it to be removed by this
  Subevent.

  _Note that an Effect must also have the_ `"temporary"` _tag in order to be removed by this Subevent._
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>CancelSubevent</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--RemoveEffect-->

<details>
<summary>RemoveOriginItemTag</summary>

**RemoveOriginItemTag**

```
{
  "subevent": "remove_origin_item_tag",
  "tags": [...],
  "tag": "..."
}
```

This Subevent is dedicated to removing a tag from an item (specifically the origin item of an event).

Source: an RPGLObject removing a tag from an origin item

Target: should be the same as the source

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `tag` is the tag to be removed from the origin item.
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  _This Subevent has no special Functions with which it is compatible._

  </details>
  <br/>
</div>
</details><!--RemoveOriginItemTag-->

<details>
<summary>SavingThrow</summary>

**SavingThrow**

```
{
  "subevent": "saving_throw",
  "tags": [...],
  "difficulty_class_ability": "...",
  "save_ability": "...",
  "damage": [
    { <damage instructions> }
  ],
  "damage_on_pass": "all" | "half" | "none",
  "vampirism": {
    "damage_type": "...",
    "round_up": t/f,
    "numerator": #,
    "denominator": #
  }
}
```

This Subevent is dedicated to making a saving throw and resolving all fallout from making the save. This is a high-level
Subevent which can be referenced in an RPGLEvent template.

Source: an RPGLObject requiring that other RPGLObjects make a saving throw

Target: an RPGLObject making a saving throw

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `difficulty_class_ability` is the ability score of the source used to determine the saving throw's DC.

  `save_ability` is the ability score of the target used to perform the saving throw.

  `damage` is an array of damage instructions defining the damage dealt by the saving throw on a fail.

  `damage_on_pass` indicates how much of the Subevent's damage should be dealt to a target which passes its save. The
  saving throw can do all damage, half damage, or no damage on a successful save.

  `vampirism` indicates any vampiric properties possessed by the saving throw. This allows the source to heal some
  portion of the damage dealt by the saving throw. This field is optional, and if left unspecified, the saving throw
  will resolve without applying any vampirism.
  
  `vampirism.damage_type` indicates which damage type has the vampiric property. This field is optional, and all damage
  dealt by the saving throw will become vampiric if not specified.
  
  `vampirism.round_up` indicates whether the vampiric calculation should round up. This field defaults to `false` if not
  specified.
  
  `vampirism.numerator` is the numerator by which the vampiric damage is multiplied during calculation to determine the
  vampiric healing. This field defaults to `1` if not specified.
  
  `vampirism.denominator` is the denominator by which the vampiric damage is divided during calculation to determine the
  vampiric healing. This field defaults to `2` if not specified.

  Conditions:

  <ul>
    <li>CheckAbility</li>
  </ul>

  Functions:

  <ul>
    <li>ApplyVampirism</li>
    <li>CancelSubevent</li>
    <li>GrantAdvantage</li>
    <li>GrantDisadvantage</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--SavingThrow-->

<details>
<summary>TakeResource</summary>

**TakeResource**

```
{
  "subevent": "take_resource",
  "tags": [...],
  "resource_tag": "...",
  "count": #
}
```

This Subevent is dedicated to taking one or more RPGLResource objects away from the target according to their resource
ID.

Source: an RPGLObject taking away a RPGLResource

Target: an RPGLObject having its RPGLResource taken away

This Subevent **CAN** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `subevent` is the subevent ID.
  
  `tags` is an array of tags which describe the Subevent.
  
  `resource_tag` is the tag which a Resource must possess in order to be taken away by this Subevent.

  _NOTE: this Subevent can only remove an RPGLResource if it has the `"temporary"` tag, typically added automatically by
  the `GiveResource` Subevent._

  `count` indicates the number of matching Resources to be taken away by this Subevent. When not specified, this
  Subevent takes away all matching Resources.
  
  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  _This Subevent has no special Functions with which it is compatible._

  </details>
  <br/>
</div>
</details><!--TakeResource-->

<details>
<summary>TemporaryHitPointCollection</summary>

**TemporaryHitPointCollection**

This Subevent is dedicated to collecting unrolled temporary hit point dice and bonuses.

Source: an RPGLObject preparing to give temporary hit points

Target: an RPGLObject which will later receive the collected temporary hit points

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>AddTemporaryHitPoints</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--TemporaryHitPointCollection-->

<details>
<summary>TemporaryHitPointDelivery</summary>

**TemporaryHitPointDelivery**

This Subevent is dedicated to delivering a quantity of temporary hit points to an RPGLObject.

Source: an RPGLObject giving temporary hit points

Target: an RPGLObject receiving temporary hit points

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>MaximizeTemporaryHitPoints</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--TemporaryHitPointDelivery-->

<details>
<summary>TemporaryHitPointRoll</summary>

**TemporaryHitPointRoll**

This abstract Subevent is dedicated to rolling temporary hit points dice.

Source: an RPGLObject rolling temporary hit point dice

Target: an RPGLObject which will later receive the rolled temporary hit points

This Subevent **CAN NOT** be referenced in an Event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Conditions:
  
  _This Subevent has no special Conditions with which it is compatible._
  
  Functions:
  
  <ul>
    <li>MaximizeTemporaryHitPoints</li>
    <li>RerollTemporaryHitPointDiceMatchingOrBelow</li>
    <li>SetTemporaryHitPointDiceMatchingOrBelow</li>
  </ul>

  </details>
  <br/>
</div>
</details><!--TemporaryHitPointRoll-->

# Conditions
RPGL provides datapack developers with a suite of Conditions used to control when Effects act upon Subevents. This
section will provide an overview for all of RPGL's Conditions, and which Subevents they apply to.

<details>
<summary>All</summary>

**All**

```
{
  "condition": "all",
  "conditions": [
    { <condition instructions> }
  ]
}
```

This Condition evaluates true if all of its nested Conditions evaluate to true.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `conditions` is a list of Conditions to be evaluated as a part of this Condition.

  Subevents:
    
  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--All-->

<details>
<summary>Any</summary>

**Any**

```
{
  "condition": "any",
  "conditions": [
    { <condition instructions> }
  ]
}
```

This Condition evaluates true if one or more of its nested Conditions evaluate to true.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `conditions` is a list of Conditions to be evaluated as a part of this Condition.
  
  Subevents:
  
  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--Any-->

<details>
<summary>CheckAbility</summary>

**CheckAbility**

```
{
  "condition": "check_ability",
  "ability": "..."
}
```

This Condition is dedicated to checking the ability score being used for an AbilitySubevent.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `ability` is the ability score the Subevent must use for the Condition to evaluate as true.

  Subevents:
  
  <ul>
    <li>AbilityCheck</li>
    <li>AbilitySave</li>
    <li>AttackRoll</li>
    <li>CalculateAbilityScore</li>
    <li>SavingThrow</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--CheckAbility-->

<details>
<summary>CheckSkill</summary>

**CheckSkill**

```
{
  "condition": "check_skill",
  "skill": "..."
}
```

This Condition is dedicated to checking the skill being used for a AbilityCheck.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `skill` is the skill which must be used by the Subevent for the Condition to evaluate as true.
  
  Subevents:

  <ul>
    <li>AbilityCheck</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--CheckSkill-->

<details>
<summary>EquippedItemHasTag</summary>

**EquippedItemHasTag**

```
{
  "condition": "equipped_item_has_tag",
  "object": {
    "from": "subevent" | "effect",
    "object": "source" | "target"
  },
  "slot": "...",
  "tag": "..."
}
```

This Condition is dedicated to evaluating whether an equipped item has a particular tag.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `object` indicates the Object being checked for the equipped Item.

  `object.from` indicates whether the Object in question is determined from the perspective of the Subevent or the
  Condition.

  `object.object` indicates whether the Object is the source or the target viewed from the indicated perspective.

  `slot` is the equipment slot being checked for an Item with a particular tag.

  `tag` is the tag which must be possessed by the target Item in order for the Condition to evaluate as true.
  
  Subevents:

  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--EquippedItemHasTag-->

<details>
<summary>IncludesDamageType</summary>

**IncludesDamageType**

```
{
  "condition": "includes_damage_type",
  "damage_type": "..."
}
```

This Condition is dedicated to checking if a DamageTypeSubevent Subevent includes a given damage type.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage_type` indicates which damage type must be included in the Subevent for the Condition to evaluate as true.
  
  Subevents:

  <ul>
    <li>CriticalDamageCollection</li>
    <li>DamageAffinity</li>
    <li>DamageCollection</li>
    <li>DamageDelivery</li>
    <li>DamageRoll</li>
    <li>DealDamage</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--IncludesDamageType-->

<details>
<summary>Invert</summary>

**Invert**

```
{
  "condition": "invert",
  "invert": { <condition instructions> }
}
```

This Condition ...

<div class="indent">
  <details>
  <summary>Read more</summary>

  `invert` is the condition which must evaluate false in order for this Condition to evaluate as true.
  
  Subevents:

  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--Invert-->

<details>
<summary>IsObjectsTurn</summary>

**IsObjectsTurn**

```
{
  "condition": "is_objects_turn",
  "object": {
    "from": "subevent" | "effect",
    "object": "source" | "target"
  }
}
```

This Condition is dedicated to determining whether it is an RPGLObject's turn.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `object` indicates which Object's turn it must be in order for this Condition to evaluate as true.

  `object.from` indicates whether the Object in question is determined from the perspective of the Subevent or the
  Condition.
  
  `object.object` indicates whether the Object is the source or the target viewed from the indicated perspective.
  
  Subevents:

  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--IsObjectsTurn-->

<details>
<summary>ObjectAbilityScoreComparison</summary>

**ObjectAbilityScoreComparison**

```
{
  "condition": "object_ability_score_comparison",
  "object": {
    "from": "subevent" | "effect",
    "object": "source" | "target"
  },
  "ability": "...",
  "comparison": "<" | ">" | "<=" | ">=" | "="
  "compare_to": #,
}
```

This Condition is dedicated to comparing an RPGLObject's ability score against a particular value.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `object` indicates which Object's ability score is being checked.
  
  `object.from` indicates whether the Object in question is determined from the perspective of the Subevent or the
  Condition.
  
  `object.object` indicates whether the Object is the source or the target viewed from the indicated perspective.

  `ability` is the ability score being checked.

  `comparison` indicates the comparison being performed between the ability score and the target value.

  `compare_to` is the value the ability score is being compared against.
  
  Subevents:

  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--ObjectAbilityScoreComparison-->

<details>
<summary>ObjectHasTag</summary>

**ObjectHasTag**

```
{
  "condition": "object_has_tag",
  "object": {
    "from": "subevent" | "effect",
    "object": "source" | "target
  },
  "tag": "..."
}
```

This Condition is dedicated to evaluating whether a particular RPGLObject has a particular tag.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `object` indicates which Object's ability score is being checked.
  
  `object.from` indicates whether the Object in question is determined from the perspective of the Subevent or the
  Condition.
  
  `object.object` indicates whether the Object is the source or the target viewed from the indicated perspective.

  `tag` is the tag which the indicated Object must possess in order for the Condition to evaluate as true.
  
  Subevents:

  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--ObjectHasTag-->

<details>
<summary>ObjectsMatch</summary>

**ObjectsMatch**

```
{
  "condition": "objects_match",
  "effect": "source" | "target",
  "subevent": "source" | "target"
}
```

This Condition ...

<div class="indent">
  <details>
  <summary>Read more</summary>

  `effect` indicates whether the Effect's source or target is used in the comparison.

  `subevent` indicates whether the Subevent's source or target is used in the comparison.
  
  Subevents:
  
  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--ObjectsMatch-->

<details>
<summary>ObjectWieldingOriginItem</summary>

**ObjectWieldingOriginItem**

```
{
  "condition": "object_wielding_origin_item",
  "object": {
    "from": "subevent" | "effect",
    "object": "source" | "target"
  }
}
```

This Condition returns true if the indicated object is wielding the origin item of the RPGLEffect, if the RPGLEffect has
an origin item.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `object` indicates which Object is being checked for wielding the Effect's origin item.
  
  `object.from` indicates whether the Object in question is determined from the perspective of the Subevent or the
  Condition.
  
  `object.object` indicates whether the Object is the source or the target viewed from the indicated perspective.
  
  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--ObjectWieldingOriginItem-->

<details>
<summary>OriginItemHasTag</summary>

**OriginItemHasTag**

```
{
  "condition": "origin_item_has_tag",
  "origin_item": "subevent" | "effect",
  "tag": "..."
}
```

This Condition is dedicated to evaluating whether an origin item has a particular tag.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `origin_item` indicates whether the origin item comes from the Subevent or the Effect.

  `tag` is a tag which must be possessed by the origin item in order for the Condition to evaluate as true.
  
  Subevents:

  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--OriginItemHasTag-->

<details>
<summary>OriginItemsMatch</summary>

**OriginItemsMatch**

```
{
  "condition": "origin_items_match"
}
```

This Condition is dedicated to comparing the origin items of an RPGLEvent and an RPGLEffect to determine if the effect
was produced by the same item being used to perform an event.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Subevents:

  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--OriginItemsMatch-->

<details>
<summary>SubeventHasTag</summary>

**SubeventHasTag**

```
{
  "condition": "subevent_has_tag",
  "tag": "..."
}
```

This Condition evaluates true if the subevent contains a specified tag.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `tag` is a tag which the Subevent must possess in order for the Condition to evaluate as true.
  
  Subevents:

  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--SubeventHasTag-->

# Functions
RPGL provides datapack developers with a suite of Functions used to control what Effects do when they act upon
Subevents. This section will provide an overview for all of RPGL's Functions, and which Subevents they apply to.

<details>
<summary>AddAttackAbility</summary>

**AddAttackAbility**

```
{
  "function": "add_attack_ability",
  "ability": "..."
}
```

This Function is dedicated to adding abilities to AttackAbilityCollection subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `ability` is the ability to be added to the AttackAbilityCollection Subevent.
  
  Subevents:
  
  <ul>
    <li>AttackAbilityCollection</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--AddAttackAbility-->

<details>
<summary>AddBonus</summary>

**AddBonus**

```
{
  "function": "add_bonus",
  "bonus": { <bonus instructions> }
}
```

This Function is dedicated to adding a bonus to Calculation Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `bonus` specifies how large of a bonus to add to the Subevent.
  
  Subevents:

  <ul>
    <li>CalculateAbilityScore</li>
    <li>CalculateBaseArmorClass</li>
    <li>CalculateCriticalHitThreshold</li>
    <li>CalculateEffectiveArmorClass</li>
    <li>CalculateMaximumHitPoints</li>
    <li>CalculateProficiencyBonus</li>
    <li>CalculateSaveDifficultyClass</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--AddBonus-->

<details>
<summary>AddDamage</summary>

**AddDamage**

```
{
  "function": "add_damage",
  "damage": { <damage instructions> }
}
```

This Function is dedicated to adding to a DamageCollection or a CriticalDamageCollection Subevent.

_Note that while this Function can add negative bonuses, it can not add "negative dice."_

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage` specifies what damage to add to the Subevent.
  
  Subevents:

  <ul>
    <li>CriticalDamageCollection</li>
    <li>DamageCollection</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--AddDamage-->

<details>
<summary>AddEvent</summary>

**AddEvent**

```
{
  "function": "add_event",
  "event": "...",
  "source": {
    "from": "subevent" | "effect",
    "object": "source" | "target"
  }
}
```

This Function is dedicated to adding a RPGLEvent to GetEvents Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `event` is the Event being added to the GetEvents Subevent.

  `source` specifies which Object serves as the source of the granted Event.

  `source.from` indicated whether the Event source is to be assigned from the perspective of the Subevent or the Effect.

  `source.object` indicates whether the source of the Event is the source or the target from the indicated perspective.
  
  Subevents:

  <ul>
    <li>GetEvents</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--AddEvent-->

<details>
<summary>AddHealing</summary>

**AddHealing**

```
{
  "function": "add_healing",
  "healing": { <healing instructions> }
}
```

This Function is dedicated to adding some amount of healing to a HealingCollection Subevent.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `healing` indicates how much healing to add to the Subevent.
  
  Subevents:

  <ul>
    <li>HealingCollection</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--AddHealing-->

<details>
<summary>AddObjectTag</summary>

**AddObjectTag**

```
{
  "function": "add_object_tag",
  "tag": "..."
}
```

This Function is dedicated to adding a tag to GetObjectTags Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `tag` is the tag to be added to the GetObjectTags Subevent.
  
  Subevents:

  <ul>
    <li>GetObjectTags</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--AddObjectTag-->

<details>
<summary>AddSubeventTag</summary>

**AddSubeventTag**

```
{
  "function": "add_subevent_tag",
  "tag": "..."
}
```

This Function is dedicated to adding tags to Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `tag` is the tag to be added to the Subevent.

  Subevents:

  _This Function can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--AddSubeventTag-->

<details>
<summary>AddTemporaryHitPoints</summary>

**AddTemporaryHitPoints**

```
{
  "function": "add_temporary_hit_points",
  "temporary_hit_points": { <temporary hit points instructions> }
}
```

This Function is dedicated to adding some amount of temporary hit points to a TemporaryHitPointCollection Subevent.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `temporary_hit_points` indicates how many temporary hit points to add to the Subevent.

  Subevents:

  <ul>
    <li>TemporaryHitPointCollection</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--AddTemporaryHitPoints-->

<details>
<summary>ApplyVampirism</summary>

**ApplyVampirism**

```
{
  "function": "apply_vampirism",
  "vampirism": {
    "damage_type": "...",
    "round_up": t/f,
    "numerator": #,
    "denominator": #
  }
}
```

This Function is dedicated to applying vampirism to AttackRoll, DealDamage, and SavingThrow Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `vampirism` indicates the vampiric properties to be applied to the Subevent. This field defaults to `{}` if not
  specified.

  `vampirism.damage_type` indicates which damage type has the vampiric property. This field is optional, and all damage
  dealt by the Subevent will become vampiric if not specified.

  `vampirism.round_up` indicates whether the vampiric calculation should round up. This field defaults to `false` if not
  specified.

  `vampirism.numerator` is the numerator by which the vampiric damage is multiplied during calculation to determine the
  vampiric healing. This field defaults to `1` if not specified.

  `vampirism.denominator` is the denominator by which the vampiric damage is divided during calculation to determine the
  vampiric healing. This field defaults to `2` if not specified.

  Subevents:

  <ul>
    <li>AttackRoll</li>
    <li>DealDamage</li>
    <li>SavingThrow</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--ApplyVampirism-->

<details>
<summary>CancelSubevent</summary>

**CancelSubevent**

```
{
  "function": "cancel_subevent",
}
```

This Function is dedicated to canceling cancelable Subevents.

_Note that this Function is not robustly supported and may lead to unintended behavior depending upon the order in which
Effects are resolved during execution._

<div class="indent">
  <details>
  <summary>Read more</summary>

  Subevents:

  <ul>
    <li>AbilityCheck</li>
    <li>AbilitySave</li>
    <li>AttackRoll</li>
    <li>CriticalDamageConfirmation</li>
    <li>DealDamage</li>
    <li>GiveEffect</li>
    <li>GiveTemporaryHitPoints</li>
    <li>Heal</li>
    <li>InfoSubevent</li>
    <li>RemoveEffect</li>
    <li>SavingThrow</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--CancelSubevent-->

<details>
<summary>EndEffect</summary>

**EndEffect**

```
{
  "function": "end_effect"
}
```

This Function is dedicated to ending the RPGLEffect containing it.

_Note that this Function may be removed in the future in favor of the RemoveEffect Subevent._

<div class="indent">
  <details>
  <summary>Read more</summary>

  Subevents:
  
  _This Condition can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--EndEffect-->

<details>
<summary>GiveExpertise</summary>

**GiveExpertise**

```
{
  "function": "give_expertise"
}
```

This Function is dedicated to giving expertise to AbilityCheck Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Subevents:

  <ul>
    <li>AbilityCheck</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--GiveExpertise-->

<details>
<summary>GiveHalfProficiency</summary>

**GiveHalfProficiency**

```
{
  "function": "give_half_proficiency"
}
```

This Function is dedicated to giving half proficiency to AbilityCheck Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Subevents:

  <ul>
    <li>AbilityCheck</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--GiveHalfProficiency-->

<details>
<summary>GiveProficiency</summary>

**GiveProficiency**

```
{
  "function": "give_proficiency"
}
```

This Function is dedicated to giving proficiency to AbilityCheck Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Subevents:

  <ul>
    <li>AbilityCheck</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--GiveProficiency-->

<details>
<summary>GrantAdvantage</summary>

**GrantAdvantage**

```
{
  "function": "grant_advantage"
}
```

This Function is dedicated to granting advantage to Roll Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Subevents:

  <ul>
    <li>AbilityCheck</li>
    <li>AttackRoll</li>
    <li>SavingThrow</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--GrantAdvantage-->

<details>
<summary>GrantDisadvantage</summary>

**GrantDisadvantage**

```
{
  "function": "grant_disadvantage"
}
```

This Function is dedicated to granting disadvantage to Roll Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Subevents:

  <ul>
    <li>AbilityCheck</li>
    <li>AttackRoll</li>
    <li>SavingThrow</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--GrantDisadvantage-->

<details>
<summary>GrantImmunity</summary>

**GrantImmunity**

```
{
  "function": "grant_immunity",
  "damage_type": "..."
}
```

This Function is dedicated to granting immunity to a damage type as indicated by a DamageAffinity Subevent.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage_type` indicates which damage type this Function grants immunity for.
  
  Subevents:

  <ul>
    <li>DamageAffinity</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--GrantImmunity-->

<details>
<summary>GrantResistance</summary>

**GrantResistance**

```
{
  "function": "grant_resistance",
}
```

This Function is dedicated to granting resistance to a damage type as indicated by a DamageAffinity Subevent.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage_type` indicates which damage type this Function grants resistance for.
  
  Subevents:

  <ul>
    <li>DamageAffinity</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--GrantResistance-->

<details>
<summary>GrantVulnerability</summary>

**GrantVulnerability**

```
{
  "function": "grant_vulnerability",
  "damage_type": "..."
}
```

This Function is dedicated to granting vulnerability to a damage type as indicated by a DamageAffinity Subevent.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage_type` indicates which damage type this Function grants vulnerability for.
  
  Subevents:

  <ul>
    <li>DamageAffinity</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--GrantVulnerability-->

<details>
<summary>InvokeSubevent</summary>

**InvokeSubevent**

```
{
  "function": "invoke_subevent",
  "source": {
    "from": "subevent" | "effect",
    "object": "source" | "target"
  },
  "targets": [
    {
      "from": "subevent" | "effect",
      "object": "source" | "target"
    }
  ],
  "subevent": { <subevent instructions> }
}
```

This Function is dedicated to invoking a particular Subevent. This Function allows for the fine control of the
Subevent's source and targets.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `source` indicates which Object is to be treated as the source for the Subevent invoked by this Function.

  `source.from` indicates whether the source is taken from the perspective of the Subevent or the Effect.

  `source.object` indicates whether the source is the source or the target taken from the indicated perspective.

  `targets` is an array of Objects to be targeted by the Subevent invoked by this Function.

  `targets[#]` is a specific Object targeted by the Subevent invoked by this Function.

  `targets[#].from` indicates whether the target is taken from the perspective of the Subevent or the Effect.

  `targets[#].object` indicates whether the target is the source or the target taken from the indicated perspective.

  `subevent` contains the instructions for the Subevent to e invoked by this Function using the specified source and
  target(s).
  
  Subevents:
  
  _This Function can be used in context of any Subevent._

  </details>
  <br/>
</div>

</details><!--InvokeSubevent-->

<details>
<summary>MaximizeDamage</summary>

**MaximizeDamage**

```
{
  "function": "maximize_damage",
  "damage_type": "..."
}
```

This Function is dedicated to maximizing the damage dice of DamageRoll and DamageDelivery Subevents. If a damage type is
specified, only damage dice of that type will be maximized.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage_type` indicates which damage type should be maximized by this Function. If not specified, all damage is
  maximized, regardless of damage type.
  
  Subevents:

  <ul>
    <li>DamageDelivery</li>
    <li>DamageRoll</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--MaximizeDamage-->

<details>
<summary>MaximizeHealing</summary>

**MaximizeHealing**

```
{
  "function": "maximize_healing"
}
```

This Function is dedicated to maximizing the healing dice of HealingRoll and HealingDelivery Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Subevents:

  <ul>
    <li>HealingDelivery</li>
    <li>HealingRoll</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--MaximizeHealing-->

<details>
<summary>MaximizeTemporaryHitPoints</summary>

**MaximizeTemporaryHitPoints**

```
{
  "function": "maximize_temporary_hit_points"
}
```

This Function is dedicated to maximizing the temporary hit point dice of TemporaryHitPointRoll and
TemporaryHitPointDelivery Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  Subevents:

  <ul>
    <li>TemporaryHitPointDelivery</li>
    <li>TemporaryHitPointRoll</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--MaximizeTemporaryHitPoints-->

<details>
<summary>RerollDamageDiceMatchingOrBelow</summary>

**RerollDamageDiceMatchingOrBelow**

```
{
  "function": reroll_damage_dice_matches_or_below",
  "damage_type": "...",
  "threshold": #
}
```

This function is dedicated to re-rolling typed damage dice matching or below a certain threshold in DamageRoll Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage_type` indicates which damage type's dice should be rerolled if they don't exceed the threshold. If not
  specified, this causes all damage dice which don't exceed the threshold to be rerolled, regardless of damage type.

  `threshold` is the value which a damage die must exceed in order to not be rerolled by this Function.
  
  Subevents:

  <ul>
    <li>DamageRoll</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--RerollDamageDiceMatchingOrBelow-->

<details>
<summary>RerollHealingDiceMatchingOrBelow</summary>

**RerollHealingDiceMatchingOrBelow**

```
{
  "function": "reroll_healing_dice_matching_or_below",
  "threshold": #
}
```

This function is dedicated to re-rolling healing dice matching or below a certain threshold in HealingRoll Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `threshold` is the value which a healing die must exceed in order to not be rerolled by this Function.
  
  Subevents:

  <ul>
    <li>HealingRoll</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--RerollHealingDiceMatchingOrBelow-->

<details>
<summary>RerollTemporaryHitPointDiceMatchingOrBelow</summary>

**RerollTemporaryHitPointDiceMatchingOrBelow**

```
{
  "function": "reroll_temporary_hit_point_dice_matching_or_below",
  "threshold": #
}
```

This function is dedicated to re-rolling temporary hit point dice matching or below a certain threshold in
TemporaryHitPointRoll Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `threshold` is the value which a temporary hit point die must exceed in order to not be rerolled by this Function.
  
  Subevents:

  <ul>
    <li>TemporaryHitPointRoll</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--RerollTemporaryHitPointDiceMatchingOrBelow-->

<details>
<summary>RevokeImmunity</summary>

**RevokeImmunity**

```
{
  "function": "revoke_immunity",
  "damage_type": "..."
}
```

This Function is dedicated to revoking immunity to a damage type as indicated by a DamageAffinity Subevent.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage_type` indicates the damage type whose immunity is being revoked. If not specified, every damage type will have
  its immunity revoked.
  
  Subevents:

  <ul>
    <li>DamageAffinity</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--RevokeImmunity-->

<details>
<summary>RevokeResistance</summary>

**RevokeResistance**

```
{
  "function": "revoke_resistance",
  "damage_type": "..."
}
```

This Function is dedicated to revoking resistance to a damage type as indicated by a DamageAffinity Subevent.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage_type` indicates the damage type whose resistance is being revoked. If not specified, every damage type will
  have its resistance revoked.
  
  Subevents:

  <ul>
    <li>DamageAffinity</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--RevokeResistance-->

<details>
<summary>RevokeVulnerability</summary>

**RevokeVulnerability**

```
{
  "function": "revoke_vulnerability",
  "damage_type": "..."
}
```

This Function is dedicated to revoking vulnerability to a damage type as indicated by a DamageAffinity Subevent.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage_type` indicates the damage type whose vulnerability is being revoked. If not specified, every damage type will
  have its vulnerability revoked.
  
  Subevents:

  <ul>
    <li>DamageAffinity</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--RevokeVulnerability-->

<details>
<summary>SetBase</summary>

**SetBase**

```
{
  "function": "set_base",
  "base": { <base instructions> }
}
```

This Function is dedicated to assigning the base field of Calculation Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `base` contains the instructions for defining the base value assigned to the Subevent.
  
  Subevents:

  <ul>
    <li>AbilityCheck</li>
    <li>AttackRoll</li>
    <li>CalculateAbilityScore</li>
    <li>CalculateBaseArmorClass</li>
    <li>CalculateCriticalHitThreshold</li>
    <li>CalculateEffectiveArmorClass</li>
    <li>CalculateMaximumHitPoints</li>
    <li>CalculateProficiencyBonus</li>
    <li>CalculateSaveDifficultyClass</li>
    <li>SavingThrow</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--SetBase-->

<details>
<summary>SetDamageDiceMatchingOrBelow</summary>

**SetDamageDiceMatchingOrBelow**

```
{
  "function": "set_damage_dice_matching_or_below",
  "damage_type": "...",
  "threshold": #,
  "set": #
}
```

This function is dedicated to setting typed damage dice matching or below a certain threshold in DamageRoll Subevents to
a given value.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `damage_type` indicates which damage type's dice will be set if they don't exceed the threshold. If not specified,
  this will set all qualifying damage dice regardless of damage type.
  `threshold` is the value which a die must exceed in order to not be set by this Function.
  `set` is the value qualifying dice will be set to by this Function.
  
  Subevents:

  <ul>
    <li>DamageRoll</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--SetDamageDiceMatchingOrBelow-->

<details>
<summary>SetHealingDiceMatchingOrBelow</summary>

**SetHealingDiceMatchingOrBelow**

```
{
  "function": "set_healing_dice_matching_or_below",
  "threshold": #,
  "set": #
}
```

This function is dedicated to setting healing dice matching or below a certain threshold in HealingRoll Subevents to a
given value.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `threshold` is the value which a die must exceed in order to not be set by this Function.
  `set` is the value qualifying dice will be set to by this Function.
  
  Subevents:
  
  _This Condition can be used in context of any Subevent._

  <ul>
    <li>HealingRoll</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--SetHealingDiceMatchingOrBelow-->

<details>
<summary>SetMinimum</summary>

**SetMinimum**

```
{
  "function": "set_minimum",
  "minimum": { <minimum instructions> }
}
```

This Function is dedicated to assigning the minimum field of Calculation Subevents.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `minimum` contains the instructions for defining the minimum value assigned to the Subevent.
  
  Subevents:

  <ul>
    <li>AbilityCheck</li>
    <li>AttackRoll</li>
    <li>CalculateAbilityScore</li>
    <li>CalculateBaseArmorClass</li>
    <li>CalculateCriticalHitThreshold</li>
    <li>CalculateEffectiveArmorClass</li>
    <li>CalculateMaximumHitPoints</li>
    <li>CalculateProficiencyBonus</li>
    <li>CalculateSaveDifficultyClass</li>
    <li>SavingThrow</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--SetMinimum-->

<details>
<summary>SetTemporaryHitPointDiceMatchingOrBelow</summary>

**SetTemporaryHitPointDiceMatchingOrBelow**

```
{
  "function": "set_temporary_hit_point_dice_matching_or_below",
  "threshold": #,
  "set": #
}
```

This function is dedicated to setting temporary hit point dice matching or below a certain threshold in
TemporaryHitPointRoll Subevents to a given value.

<div class="indent">
  <details>
  <summary>Read more</summary>

  `threshold` is the value which a die must exceed in order to not be set by this Function.

  `set` is the value qualifying dice will be set to by this Function.
  
  Subevents:

  <ul>
    <li>TemporaryHitPointRoll</li>
  </ul>

  </details>
  <br/>
</div>

</details><!--SetTemporaryHitPointDiceMatchingOrBelow-->

<br />
