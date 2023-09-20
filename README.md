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

- **RPGLClass.** This class represents any role-playing class (as distinct from Java classes). This includes the likes
  of wizards, fighters, and rangers. The client is encouraged to use the `RPGLFactory.getClass(...)` method to get
  references to objects of this type (this data type is not designed to have new instances created by the client, and
  doing so is not recommended).
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
- **RPGLRace.** This class represents any role-playing race. This includes the likes of humans, elves, and pixies. The
  client is encouraged to use the `RPGLFactory.getRace(...)` method to get references to objects of this type (this data
  type is not designed to have new instances created by the client, and doing so is not recommended).
- **RPGLResource.** This class represents any expendable, non-item resources which might be expended by an object during
  a turn. This includes the likes of spell slots, actions, ki points, and action surges. The client is encouraged to use
  the `RPGLFactory.newResource(...)` method to create objects of this type.

  
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
may be left out, and any additional filed or directories will be ignored by RPGL.

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
conditions the Effect will be applied, and what the Effect's effect is when applied. Note that an Effect may have
multiple behaviors for a single Subevent if multiple JSON objects are provided for it.

_As a rule of thumb, every Effect should have an `"objects_match"` Condition for each behavior to ensure that the
Effect is restricted to only affect Subevents originating from or directed to the intended subject._

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

`tags` is a list of tags which describe the item. This field defaults to `[]` if not specified.

`weight` is the weight of the item. This field defaults to 0 if not specified.

`cost` is the cost of the item. This field defaults to 0 if not specified.

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

</details>

## RPGLObject templates

## RPGLResource templates