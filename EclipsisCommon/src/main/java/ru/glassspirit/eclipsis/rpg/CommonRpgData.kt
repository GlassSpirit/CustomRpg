package ru.glassspirit.eclipsis.rpg

data class RpgGroupType(
        val name: String = "dummyGroupType"
)

data class RpgGroupDefinition(
        val name: String = "dummyGroup",
        val description: String = "dummyDescription",
        val groupType: RpgGroupType = RpgGroupType()
)

data class RpgGroup(
        val groupDefinition: RpgGroupDefinition = RpgGroupDefinition(),
        val level: Int = 0,
        val experiences: Double = 0.0,
        val skillPoints: Int = 0,
        val usedSkillPoints: Int = 0
)

data class RpgCharacterBase(
        val name: String,
        val attributePoints: Int,
        val usedAttributePoints: Int,
        val canResetskills: Boolean,
        //val rpgSkills: Set<RpgSkill> = HashSet()
        //val baseCharacterAttribute: Set<RpgAttribute> = HashSet()
        val X: Int,
        val Y: Int,
        val Z: Int,
        val world: String
)

data class RpgCharacter(
        val rpgCharacterBase: RpgCharacterBase,
        val rpgGroups: Set<RpgGroup> = HashSet(),
        val health: Double,
        val mana: Double,
        val cooldowns: Map<String, Long> = HashMap(),
        val primaryClass: RpgGroup
)

data class RpgEffect(
        val name: String
)

