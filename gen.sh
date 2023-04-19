#!/bin/zsh

MODID="pillagertrading"
RESOURCESDIR="src/main/resources"

function gen_blockstate() {
    echo "{"
    echo "  \"variants\": {"
    echo "    \"\": { \"model\": \"${MODID}:block/${1}\" }"
    echo "  }"
    echo "}"
}

function gen_woodstyle_blockstate() {
  echo "{"
  echo "  \"variants\": {"
  echo "    \"axis=x\": {"
  echo "      \"model\": \"${MODID}:block/${1}\","
  echo "      \"x\": 90,"
  echo "      \"y\": 90"
  echo "    },"
  echo "    \"axis=y\": {"
  echo "      \"model\": \"${MODID}:block/${1}\""
  echo "    },"
  echo "    \"axis=z\": {"
  echo "      \"model\": \"${MODID}:block/${1}\","
  echo "      \"x\": 90"
  echo "    }"
  echo "  }"
  echo "}"
}

function gen_roatated_blockstate() {
  echo "{"
  echo "  \"variants\": {"
  echo "    \"\": ["
  echo "      {"
  echo "        \"model\": \"${MODID}:block/${1}\""
  echo "      },"
  echo "      {"
  echo "        \"model\": \"${MODID}:block/${1}\","
  echo "        \"y\": 90"
  echo "      },"
  echo "      {"
  echo "        \"model\": \"${MODID}:block/${1}\","
  echo "        \"y\": 180"
  echo "      },"
  echo "      {"
  echo "        \"model\": \"${MODID}:block/${1}\","
  echo "        \"y\": 270"
  echo "      }"
  echo "    ]"
  echo "  }"
  echo "}"
}

function gen_model_cube_all() {
  echo "{"
  echo "  \"parent\": \"block/cube_all\","
  echo "  \"textures\": {"
  echo "    \"all\": \"${MODID}:block/${1}\""
  echo "  }"
  echo "}"
}

function gen_model_woodstyle() {
  echo "{"
  echo "  \"parent\": \"minecraft:block/cube_column\","
  echo "  \"textures\": {"
  echo "    \"end\": \"${MODID}:block/${1}\","
  echo "    \"side\": \"${MODID}:block/${1}\""
  echo "  }"
  echo "}"
}

function gen_model_grass() {
  echo "{"
  echo "  \"parent\": \"block/cross\","
  echo "  \"render_type\": \"cutout\","
  echo "  \"textures\": {"
  echo "    \"cross\": \"${MODID}:block/${1}\""
  echo "  }"
  echo "}"
}

function gen_model_blockitem() {
  echo "{"
  echo "  \"parent\": \"${MODID}:block/${1}\""
  echo "}"
}

function gen_model_item() {
  echo "{"
  echo "  \"parent\": \"minecraft:item/generated\","
  echo "  \"textures\": {"
  echo "    \"layer0\": \"${MODID}:${1}/${2}\""
  echo "  }"
  echo "}"
}

function gen_block_loot_table() {
  echo "{"
  echo "  \"type\": \"minecraft:block\","
  echo "  \"pools\": ["
  echo "    {"
  echo "      \"bonus_rolls\": 0,"
  echo "      \"conditions\": ["
  echo "        {"
  echo "          \"condition\": \"minecraft:survives_explosion\""
  echo "        }"
  echo "      ],"
  echo "      \"entries\": ["
  echo "        {"
  echo "          \"type\": \"minecraft:item\","
  echo "          \"name\": \"${MODID}:${1}\""
  echo "        }"
  echo "      ],"
  echo "      \"rolls\": 1"
  echo "    }"
  echo "  ]"
  echo "}"
}

function gen_silk_touch_loot_table() {
  echo "{"
  echo "  \"type\": \"minecraft:block\","
  echo "  \"pools\": ["
  echo "    {"
  echo "      \"bonus_rolls\": 0.0,"
  echo "      \"entries\": ["
  echo "        {"
  echo "          \"type\": \"minecraft:alternatives\","
  echo "          \"children\": ["
  echo "            {"
  echo "              \"type\": \"minecraft:item\","
  echo "              \"conditions\": ["
  echo "                {"
  echo "                  \"condition\": \"minecraft:match_tool\","
  echo "                  \"predicate\": {"
  echo "                    \"enchantments\": ["
  echo "                      {"
  echo "                        \"enchantment\": \"minecraft:silk_touch\","
  echo "                        \"levels\": {"
  echo "                          \"min\": 1"
  echo "                        }"
  echo "                      }"
  echo "                    ]"
  echo "                  }"
  echo "                }"
  echo "              ],"
  echo "              \"name\": \"${MODID}:${1}\""
  echo "            },"
  echo "            {"
  echo "              \"type\": \"minecraft:item\","
  echo "              \"conditions\": ["
  echo "                {"
  echo "                  \"condition\": \"minecraft:survives_explosion\""
  echo "                }"
  echo "              ],"
  echo "              \"name\": \"${2}\""
  echo "            }"
  echo "          ]"
  echo "        }"
  echo "      ],"
  echo "      \"rolls\": 1.0"
  echo "    }"
  echo "  ]"
  echo "}"
}

function gen_mine_tool() {
  cat ${RESOURCESDIR}/data/minecraft/tags/blocks/${1}.json | head -n -2 | head -c -1
  echo ","
  echo "    \"${MODID}:${2}\""
  echo "  ]"
  echo "}"
}

function add_mine_tool() {
  gen_mine_tool ${1} ${2} > ${RESOURCESDIR}/data/minecraft/tags/blocks/${1}.json2
  pushd ${RESOURCESDIR}/data/minecraft/tags/blocks
  rm ${1}.json
  mv ${1}.json2 ${1}.json
  popd
}

function gen_lang() {
    cat ${RESOURCESDIR}/assets/${MODID}/lang/en_us.json | head -n -1 | head -c -1
    echo ","
    printf "  \"${1}.${MODID}.${2}\": \""
    echo ${2} | tr _ ' ' | sed -e "s/\b\(.\)/\u\1/g" | head -c -1
    echo '"'
    echo "}"
}

function add_lang() {
  gen_lang ${1} ${2} > ${RESOURCESDIR}/assets/${MODID}/lang/en_us.json.bak
  pushd ${RESOURCESDIR}/assets/${MODID}/lang/
  mv en_us.json en_us.json.bak2
  mv en_us.json.bak en_us.json
  rm en_us.json.bak2
  popd
}

function add_block() {
  for cmd in "${(@)@:2}"
  do
    if [[ "$cmd" == "fast" ]]
    then
      gen_model_cube_all ${1} > ${RESOURCESDIR}/assets/${MODID}/models/block/${1}.json
      gen_model_blockitem ${1} > ${RESOURCESDIR}/assets/${MODID}/models/item/${1}.json
      gen_block_loot_table ${1} > ${RESOURCESDIR}/data/${MODID}/loot_tables/blocks/${1}.json
      add_lang 'block' ${1}
      gen_blockstate ${1} > ${RESOURCESDIR}/assets/${MODID}/blockstates/${1}.json
    elif [[ "$cmd" == "one-state" ]]
    then
      gen_blockstate ${1} > ${RESOURCESDIR}/assets/${MODID}/blockstates/${1}.json
    elif [[ "$cmd" == "wood-state" ]]
    then
      gen_woodstyle_blockstate ${1} > ${RESOURCESDIR}/assets/${MODID}/blockstates/${1}.json
    elif [[ "$cmd" == "rotate-state" ]]
    then
      gen_rotated_blockstate ${1} > ${RESOURCESDIR}/assets/${MODID}/blockstates/${1}.json
    elif [[ "$cmd" == "model-cube-all" ]]
    then
      gen_model_cube_all ${1} > ${RESOURCESDIR}/assets/${MODID}/models/block/${1}.json
    elif [[ "$cmd" == "model-wood" ]]
    then
      gen_model_woodstyle ${1} > ${RESOURCESDIR}/assets/${MODID}/models/block/${1}.json
    elif [[ "$cmd" == "model-grass" ]]
    then
      gen_model_grass ${1} > ${RESOURCESDIR}/assets/${MODID}/models/block/${1}.json
    elif [[ "$cmd" == "item-model-3d" ]]
    then
      gen_model_blockitem ${1} > ${RESOURCESDIR}/assets/${MODID}/models/item/${1}.json
    elif [[ "$cmd" == "item-model-flat" ]]
    then
      gen_model_item 'block' ${1} > ${RESOURCESDIR}/assets/${MODID}/models/item/${1}.json
    elif [[ "$cmd" == "drop-itself" ]]
    then
      gen_block_loot_table ${1} > ${RESOURCESDIR}/data/${MODID}/loot_tables/blocks/${1}.json
    elif [[ "$cmd" == drop-silk-touch* ]]
    then
      a=${cmd#*~}
      gen_silk_touch_loot_table ${1} ${a} > ${RESOURCESDIR}/data/${MODID}/loot_tables/blocks/${1}.json
    elif [[ "$cmd" == "lang" ]]
    then
      add_lang 'block' ${1}
    elif [[ "$cmd" == "needs-iron" ]]
    then
      add_mine_tool 'needs_iron_tool' ${1}
    elif [[ "$cmd" == "needs-axe" ]]
    then
      add_mine_tool 'mineable/axe' ${1}
    elif [[ "$cmd" == "needs-pickaxe" ]]
    then
      add_mine_tool 'mineable/pickaxe' ${1}
    elif [[ "$cmd" == "needs-shovel" ]]
    then
      add_mine_tool 'mineable/shovel' ${1}
    fi
  done
}

function add_item() {
  for cmd in "${(@)@:2}"
    do
      if [[ "$cmd" == "fast" ]]
      then
        gen_model_item 'item' ${1} > ${RESOURCESDIR}/assets/${MODID}/models/item/${1}.json
        add_lang 'item' ${1}
      elif [[ "$cmd" == "simple-model" ]]
      then
        gen_model_item ${1} > ${RESOURCESDIR}/assets/${MODID}/models/item/${1}.json
      elif [[ "$cmd" == "lang" ]]
      then
        add_lang 'item' ${1}
      fi
    done
}

if [[ "${1}" == "item" ]]
then
  add_item ${(@)@:2}
elif [[ "${1}" == "block" ]]
then
  add_block ${(@)@:2}
else
  echo "Correct usage: gen.sh (item|block) name (things to generate)..."
  echo "Parameters for generators are given with a tilda like so:"
  echo "  ./gen.sh block grass_block drop-silk-touch~minecraft:dirt"
  echo "For blocks you can generate:"
  echo "  fast            - generates everything on very default settings"
  echo "  one-state       - blockstate with a single state"
  echo "  wood-state      - blockstate of that of the wood blocks"
  echo "  rotate-state    - gives random rotation in the y"
  echo "  model-cube-all  - simple cube model with texture on all sides"
  echo "  model-wood      - same texture on all sides but rotated (like wood)"
  echo "  model-grass     - cross texture like grass"
  echo "  item-model-3d   - the simple BlockItem model inherited from parent"
  echo "  item-model-flat - the flat item model from the texture"
  echo "  drop-itself     - loot table to drop itself on being destroyed"
  echo "  drop-silk-touch - drops itself with silk touch and the parameter if not"
  echo "  needs-iron      - includes it in the needs iron tools tag"
  echo "  needs-axe       - if it requires an axe tag"
  echo "  needs-pickaxe   - if it requires a pickaxe tag"
  echo "  needs-shovel    - if it requires a shovel tag"
  echo "  lang            - includes it in the language file"
  echo "For items you can generate:"
  echo "  fast         - generates everything on very default settings"
  echo "  simple-model - uses the default item model"
  echo "  lang         - includes it in the language file with changed case"
fi
