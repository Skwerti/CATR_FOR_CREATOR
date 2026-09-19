
import json, os
base = r'D:\dow\mine2_catr\catrmodd\src\main\resources\data\catr'
tags_dir = os.path.join(base, 'tags', 'item')
os.makedirs(tags_dir, exist_ok=True)

# Steel IDs
STEELS = {
    'bigcannons': 'createbigcannons:steel_ingot',
    'nuclear': 'createnuclear:steel_ingot',
    'mekanism': 'mekanism:ingot_steel',
    'tfmg': 'tfmg:steel_ingot',
    'catr': 'catr:steel_ingot',
}

MODS = ['bigcannons', 'nuclear', 'mekanism', 'tfmg']

# All combination tags (2,3,4 mods) + single-mod tags
# We'll generate tags for every non-empty subset of MODS
from itertools import combinations

tag_specs = {}
# Single-mod tags
for m in MODS:
    tag_specs[m] = ([STEELS[m]], [])
# All combinations of 2+
for r in range(2, 5):
    for combo in combinations(MODS, r):
        tag_name = 'moded_steel_' + '_'.join(combo)
        values = [STEELS[m] for m in combo]
        tag_specs[tag_name] = (values, combo)

# Write tags (no conditions to avoid empty-tag errors)
for tag_name, (values, combo) in tag_specs.items():
    p = os.path.join(tags_dir, tag_name + '.json')
    # For combo tags, include all mods in combo; for single, just that mod
    with open(p, 'w', encoding='utf-8') as f:
        json.dump({'values': values}, f, indent=2, ensure_ascii=False)
    bp = os.path.join(r'D:\dow\mine2_catr\catrmodd\build\resources\main\data\catr\tags\item', tag_name + '.json')
    with open(bp, 'w', encoding='utf-8') as f:
        json.dump({'values': values}, f, indent=2, ensure_ascii=False)
    print('Tag', tag_name, values)

# Now define 16 states for recipes
# Order: big, nuc, mek, tfmg
states = []
for i in range(16):
    bits = [(i>>3)&1, (i>>2)&1, (i>>1)&1, i&1]
    present = [MODS[j] for j,b in enumerate(bits) if b==1]
    absent = [MODS[j] for j,b in enumerate(bits) if b==0]
    # Conditions
    conds = []
    for a in absent:
        conds.append({'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':a}})
    for p in present:
        conds.append({'type':'neoforge:mod_loaded','modid':p})
    # Ingredient
    if len(present)==0:
        ing_ref = 'catr:steel_ingot'
        is_tag = False
        tag_name = None
    elif len(present)==1:
        ing_ref = STEELS[present[0]]
        is_tag = False
        tag_name = None
    else:
        # multi-mod: use tag
        combo_sorted = tuple(sorted(present))
        tag_name = 'moded_steel_' + '_'.join(combo_sorted)
        ing_ref = 'catr:' + tag_name
        is_tag = True
    # Suffix
    if len(present)==0:
        suffix = 'without'
    elif len(present)==1:
        suffix = present[0]
    elif len(present)==2:
        # special naming for common pairs
        if set(present) == {'bigcannons','nuclear'}:
            suffix = 'duo'
        else:
            suffix = '_'.join(sorted(present))
    elif len(present)==3:
        # For 3-mod combos, use descriptive suffix
        combo = tuple(sorted(present))
        if combo == ('bigcannons','mekanism','nuclear'):
            suffix = 'duo_mekanism'  # approximate
        elif combo == ('bigcannons','mekanism','tfmg'):
            suffix = 'mek_bigcannons_tfmg'
        elif combo == ('bigcannons','nuclear','tfmg'):
            suffix = 'nuc_bigcannons_tfmg'
        elif combo == ('mekanism','nuclear','tfmg'):
            suffix = 'mek_nuclear_tfmg'
        else:
            suffix = '_'.join(combo)
    else:  # 4
        suffix = 'all'
    states.append((suffix, conds, ing_ref, is_tag, tag_name))

# Helper to build recipes
def make_rolling(suffix, conds, ing_ref, is_tag):
    return {
        'neoforge:conditions': conds,
        'type': 'createaddition:rolling',
        'ingredients': [{'tag': ing_ref[5:] if ing_ref.startswith('catr:') else ing_ref} if is_tag else {'item': ing_ref}],
        'results': [{'count': 2, 'id': 'catr:steel_rod'}]
    }

# Actually for is_tag, ing_ref should be full tag id like 'catr:moded_steel_all'
# Let me fix helper

def build_rolling(suffix, conds, ing_ref, is_tag):
    ing = {'tag': ing_ref.replace('catr:','')} if is_tag else {'item': ing_ref}
    if is_tag:
        ing = {'tag': ing_ref.replace('catr:','')}
    else:
        ing = {'item': ing_ref}
    # For rolling the ingredient array should contain the item/tag
    return {
        'neoforge:conditions': conds,
        'type': 'createaddition:rolling',
        'ingredients': [ing],
        'results': [{'count': 2, 'id': 'catr:steel_rod'}]
    }

def build_pressing(suffix, conds, ing_ref, is_tag):
    ing = {'tag': ing_ref.replace('catr:','')} if is_tag else {'item': ing_ref}
    return {
        'neoforge:conditions': conds,
        'type': 'create:pressing',
        'ingredients': [ing],
        'results': [{'id': 'catr:steel_sheet'}]
    }

def build_mixing(suffix, conds, ing_ref, is_tag):
    # Mixing produces steel; conditions change but input stays coal/iron
    return {
        'neoforge:conditions': conds,
        'type': 'create:mixing',
        'ingredients': [{'item':'minecraft:coal'}, {'tag':'c:ingots/iron'}],
        'results': [{'id':'catr:steel_ingot'}]
    }

# Load ifmoded templates
with open(os.path.join(base, 'recipe/sequenced_assembly/ifmoded/durable_shutter_without.json'), 'r', encoding='utf-8') as f:
    template_dur = json.load(f)
with open(os.path.join(base, 'recipe/sequenced_assembly/ifmoded/shutter_deagle_without.json'), 'r', encoding='utf-8') as f:
    template_deg = json.load(f)

def build_durable(suffix, conds, ing_ref, is_tag):
    data = json.loads(json.dumps(template_dur))
    data['neoforge:conditions'] = conds
    ing = data.get('ingredient', {})
    if is_tag:
        ing['tag'] = ing_ref.replace('catr:', '')
        if 'item' in ing: del ing['item']
    else:
        ing['item'] = ing_ref
        if 'tag' in ing: del ing['tag']
    data['ingredient'] = ing
    return data

def build_deagle(suffix, conds, ing_ref, is_tag):
    data = json.loads(json.dumps(template_deg))
    data['neoforge:conditions'] = conds
    ing = data.get('ingredient', {})
    if is_tag:
        ing['tag'] = ing_ref.replace('catr:', '')
        if 'item' in ing: del ing['item']
    else:
        ing['item'] = ing_ref
        if 'tag' in ing: del ing['tag']
    data['ingredient'] = ing
    return data

# Generate all files
categories = {
    'recipe/rolling': ('steel_rod_', build_rolling),
    'recipe/pressing': ('steel_sheet_', build_pressing),
    'recipe/mixing': ('steel_ingot_', build_mixing),
    'recipe/sequenced_assembly/ifmoded': ('durable_shutter_', build_durable),
    'recipe/sequenced_assembly/ifmoded': ('shutter_deagle_', build_deagle),
}

# Actually need separate dirs for durable/deagle within same root
for suffix, conds, ing_ref, is_tag, tag_name in states:
    # Rolling
    p = os.path.join(base, 'recipe/rolling', 'steel_rod_' + suffix + '.json')
    with open(p, 'w', encoding='utf-8') as f:
        json.dump(build_rolling(suffix, conds, ing_ref, is_tag), f, indent=2, ensure_ascii=False)
    # Pressing
    p = os.path.join(base, 'recipe/pressing', 'steel_sheet_' + suffix + '.json')
    with open(p, 'w', encoding='utf-8') as f:
        json.dump(build_pressing(suffix, conds, ing_ref, is_tag), f, indent=2, ensure_ascii=False)
    # Mixing
    p = os.path.join(base, 'recipe/mixing', 'steel_ingot_' + suffix + '.json')
    with open(p, 'w', encoding='utf-8') as f:
        json.dump(build_mixing(suffix, conds, ing_ref, is_tag), f, indent=2, ensure_ascii=False)
    # Durable shutter
    p = os.path.join(base, 'recipe/sequenced_assembly/ifmoded', 'durable_shutter_' + suffix + '.json')
    with open(p, 'w', encoding='utf-8') as f:
        json.dump(build_durable(suffix, conds, ing_ref, is_tag), f, indent=2, ensure_ascii=False)
    # Deagle
    p = os.path.join(base, 'recipe/sequenced_assembly/ifmoded', 'shutter_deagle_' + suffix + '.json')
    with open(p, 'w', encoding='utf-8') as f:
        json.dump(build_deagle(suffix, conds, ing_ref, is_tag), f, indent=2, ensure_ascii=False)
    if suffix in ['without','bigcannons','nuclear','mekanism','tfmg','duo','all']:
        print('GEN', suffix, 'ing=', ing_ref, 'tag=', tag_name)

print('16-COMBO FULL COVERAGE DONE. Total tag files:', len(os.listdir(tags_dir)))

# Fix build functions: tags use full catr:moded_... id
# Re-defining correctly

def build_rolling(suffix, conds, ing_ref, is_tag):
    ing = {"tag": ing_ref} if is_tag else {"item": ing_ref}
    return {"neoforge:conditions": conds, "type": "createaddition:rolling", "ingredients": [ing], "results": [{"count": 2, "id": "catr:steel_rod"}]}

def build_pressing(suffix, conds, ing_ref, is_tag):
    ing = {"tag": ing_ref} if is_tag else {"item": ing_ref}
    return {"neoforge:conditions": conds, "type": "create:pressing", "ingredients": [ing], "results": [{"id": "catr:steel_sheet"}]}

def build_mixing(suffix, conds, ing_ref, is_tag):
    return {"neoforge:conditions": conds, "type": "create:mixing", "ingredients": [{"item":"minecraft:coal"}, {"tag":"c:ingots/iron"}], "results": [{"id":"catr:steel_ingot"}]}

def build_durable(suffix, conds, ing_ref, is_tag):
    import copy
    import json as _json
    with open(os.path.join(base, 'recipe/sequenced_assembly/ifmoded/durable_shutter_without.json'),'r',encoding='utf-8') as f:
        data = _json.load(f)
    data["neoforge:conditions"] = conds
    ing = data.get("ingredient", {})
    if is_tag:
        ing["tag"] = ing_ref
        if "item" in ing: del ing["item"]
    else:
        ing["item"] = ing_ref
        if "tag" in ing: del ing["tag"]
    data["ingredient"] = ing
    return data

def build_deagle(suffix, conds, ing_ref, is_tag):
    import json as _json
    with open(os.path.join(base, 'recipe/sequenced_assembly/ifmoded/shutter_deagle_without.json'),'r',encoding='utf-8') as f:
        data = _json.load(f)
    data["neoforge:conditions"] = conds
    ing = data.get("ingredient", {})
    if is_tag:
        ing["tag"] = ing_ref
        if "item" in ing: del ing["item"]
    else:
        ing["item"] = ing_ref
        if "tag" in ing: del ing["tag"]
    data["ingredient"] = ing
    return data
