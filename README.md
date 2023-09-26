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
</details><!--Heal-->

<details>
<summary>HealingCollection</summary>

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
</details><!--HealingCollection-->

<details>
<summary>HealingDelivery</summary>

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
</details><!--HealingDelivery-->

<details>
<summary>HealingRoll</summary>

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
</details><!--HealingRoll-->

<details>
<summary>InfoSubevent</summary>

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
</details><!--InfoSubevent-->

<details>
<summary>RefreshResource</summary>

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
</details><!--RefreshResource-->

<details>
<summary>RemoveEffect</summary>

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
</details><!--RemoveEffect-->

<details>
<summary>RemoveOriginItemTag</summary>

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
</details><!--RemoveOriginItemTag-->

<details>
<summary>SavingThrow</summary>

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
</details><!--SavingThrow-->

<details>
<summary>TakeResource</summary>

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
</details><!--TakeResource-->

<details>
<summary>TemporaryHitPointCollection</summary>

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
</details><!--TemporaryHitPointCollection-->

<details>
<summary>TemporaryHitPointDelivery</summary>

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
</details><!--TemporaryHitPointDelivery-->

<details>
<summary>TemporaryHitPointRoll</summary>

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
</details><!--TemporaryHitPointRoll-->

<!--# Conditions
RPGL provides datapack developers with a suite of Conditions used to control when Effects act upon Subevents. This
section will provide an overview for all of RPGL's Conditions, and which Subevents they apply to.

<details>
<summary>All</summary>
</details>

<details>
<summary>Any</summary>
</details>

<details>
<summary>CheckAbility</summary>
</details>

<details>
<summary>CheckSkill</summary>
</details>

<details>
<summary>EquippedItemHasTag</summary>
</details>

<details>
<summary>IncludesDamageType</summary>
</details>

<details>
<summary>Invert</summary>
</details>

<details>
<summary>IsObjectsTurn</summary>
</details>

<details>
<summary>ObjectAbilityScoreComparison</summary>
</details>

<details>
<summary>ObjectHasTag</summary>
</details>

<details>
<summary>ObjectWieldingOriginItem</summary>
</details>

<details>
<summary>ObjectsMatch</summary>
</details>

<details>
<summary>OriginItemHasTag</summary>
</details>

<details>
<summary>OriginItemsMatch</summary>
</details>

<details>
<summary>SubeventHasTag</summary>
</details>

# Functions
RPGL provides datapack developers with a suite of Functions used to control what Effects do when they act upon
Subevents. This section will provide an overview for all of RPGL's Functions, and which Subevents they apply to.

<details>
<summary>AddAttackAbility</summary>
</details>

<details>
<summary>AddBonus</summary>
</details>

<details>
<summary>AddDamage</summary>
</details>

<details>
<summary>AddEvent</summary>
</details>

<details>
<summary>AddHealing</summary>
</details>

<details>
<summary>AddObjectTag</summary>
</details>

<details>
<summary>AddSubeventTag</summary>
</details>

<details>
<summary>AddTemporaryHitPoints</summary>
</details>

<details>
<summary>ApplyVampirism</summary>
</details>

<details>
<summary>CancelSubevent</summary>
</details>

<details>
<summary>EndEffect</summary>
</details>

<details>
<summary>GiveExpertise</summary>
</details>

<details>
<summary>GiveHalfProficiency</summary>
</details>

<details>
<summary>GiveProficiency</summary>
</details>

<details>
<summary>GrantAdvantage</summary>
</details>

<details>
<summary>GrantDisadvantage</summary>
</details>

<details>
<summary>GrantImmunity</summary>
</details>

<details>
<summary>GrantResistance</summary>
</details>

<details>
<summary>GrantVulnerability</summary>
</details>

<details>
<summary>InvokeSubevent</summary>
</details>

<details>
<summary>MaximizeDamage</summary>
</details>

<details>
<summary>MaximizeHealing</summary>
</details>

<details>
<summary>MaximizeTemporaryHitPoints</summary>
</details>

<details>
<summary>RerollDamageDiceMatchingOrBelow</summary>
</details>

<details>
<summary>RerollHealingDiceMatchingOrBelow</summary>
</details>

<details>
<summary>RerollTemporaryHitPointDiceMatchingOrBelow</summary>
</details>

<details>
<summary>RevokeImmunity</summary>
</details>

<details>
<summary>RevokeResistance</summary>
</details>

<details>
<summary>RevokeVulnerability</summary>
</details>

<details>
<summary>SetBase</summary>
</details>

<details>
<summary>SetDamageDiceMatchingOrBelow</summary>
</details>

<details>
<summary>SetHealingDiceMatchingOrBelow</summary>
</details>

<details>
<summary>SetMinimum</summary>
</details>

<details>
<summary>SetTemporaryHitPointDiceMatchingOrBelow</summary>
</details>

<br />
-->