
import json, os
base = r'D:\dow\mine2_catr\catrmodd\src\main\resources\data\catr'

# Tag definitions
tags_dir = os.path.join(base, 'tags', 'items')
os.makedirs(tags_dir, exist_ok=True)

tag_specs = {
    'moded_steel': {
        'conditions': [{'type':'neoforge:mod_loaded','modid':'createnuclear'},
                       {'type':'neoforge:mod_loaded','modid':'createbigcannons'}],
        'values': ['createnuclear:steel_ingot','createbigcannons:steel_ingot']
    },
    'moded_steel_bigcannons_mekanism': {
        'conditions': [{'type':'neoforge:mod_loaded','modid':'createbigcannons'},
                       {'type':'neoforge:mod_loaded','modid':'mekanism'}],
        'values': ['createbigcannons:steel_ingot','mekanism:ingot_steel']
    },
    'moded_steel_nuclear_mekanism': {
        'conditions': [{'type':'neoforge:mod_loaded','modid':'createnuclear'},
                       {'type':'neoforge:mod_loaded','modid':'mekanism'}],
        'values': ['createnuclear:steel_ingot','mekanism:ingot_steel']
    },
    'moded_steel_all': {
        'conditions': [{'type':'neoforge:mod_loaded','modid':'createnuclear'},
                       {'type':'neoforge:mod_loaded','modid':'createbigcannons'},
                       {'type':'neoforge:mod_loaded','modid':'mekanism'}],
        'values': ['createnuclear:steel_ingot','createbigcannons:steel_ingot','mekanism:ingot_steel']
    },
}

for name, spec in tag_specs.items():
    path = os.path.join(tags_dir, name + '.json')
    with open(path, 'w', encoding='utf-8') as f:
        json.dump({'neoforge:conditions': spec['conditions'], 'values': spec['values']}, f, indent=2, ensure_ascii=False)
    print('TAG', name)

# Now generate 8 variants for rolling, pressing, mixing, and ifmoded durable_shutter / shutter_deagle
states = [
    # (suffix, conditions list, ingredient_ref, is_tag)
    ('without', [{'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'createbigcannons'}},
                 {'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'createnuclear'}},
                 {'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'mekanism'}}],
     'catr:steel_ingot', False),
    ('bigcannons', [{'type':'neoforge:mod_loaded','modid':'createbigcannons'},
                     {'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'createnuclear'}},
                     {'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'mekanism'}}],
     'createbigcannons:steel_ingot', False),
    ('nuclear', [{'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'createbigcannons'}},
                  {'type':'neoforge:mod_loaded','modid':'createnuclear'},
                  {'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'mekanism'}}],
     'createnuclear:steel_ingot', False),
    ('mekanism', [{'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'createbigcannons'}},
                  {'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'createnuclear'}},
                  {'type':'neoforge:mod_loaded','modid':'mekanism'}],
     'mekanism:ingot_steel', False),
    ('duo', [{'type':'neoforge:mod_loaded','modid':'createnuclear'},
              {'type':'neoforge:mod_loaded','modid':'createbigcannons'},
              {'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'mekanism'}}],
     'catr:moded_steel', True),
    ('mek_bigcannons', [{'type':'neoforge:mod_loaded','modid':'createbigcannons'},
                        {'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'createnuclear'}},
                        {'type':'neoforge:mod_loaded','modid':'mekanism'}],
     'catr:moded_steel_bigcannons_mekanism', True),
    ('mek_nuclear', [{'type':'neoforge:not','value':{'type':'neoforge:mod_loaded','modid':'createbigcannons'}},
                     {'type':'neoforge:mod_loaded','modid':'createnuclear'},
                     {'type':'neoforge:mod_loaded','modid':'mekanism'}],
     'catr:moded_steel_nuclear_mekanism', True),
    ('all', [{'type':'neoforge:mod_loaded','modid':'createnuclear'},
              {'type':'neoforge:mod_loaded','modid':'createbigcannons'},
              {'type':'neoforge:mod_loaded','modid':'mekanism'}],
     'catr:moded_steel_all', True),
]

def make_rolling(suffix, conds, ing_ref, is_tag):
    return {
        'neoforge:conditions': conds,
        'type': 'createaddition:rolling',
        'ingredients': [{'item': ing_ref} if not is_tag else {'tag': ing_ref}],
        'results': [{'count': 2, 'id': 'catr:steel_rod'}]
    }

def make_pressing(suffix, conds, ing_ref, is_tag):
    return {
        'neoforge:conditions': conds,
        'type': 'create:pressing',
        'ingredients': [{'item': ing_ref} if not is_tag else {'tag': ing_ref}],
        'results': [{'id': 'catr:steel_sheet'}]
    }

def make_mixing(suffix, conds, ing_ref, is_tag):
    # Note: mixing produces steel_ingot; for variants we keep same output but conditions change
    return {
        'neoforge:conditions': conds,
        'type': 'create:mixing',
        'ingredients': [{'item': 'minecraft:coal'}, {'tag': 'c:ingots/iron'}],
        'results': [{'id': 'catr:steel_ingot'}]
    }

def make_ifmoded_durable(suffix, conds, ing_ref, is_tag):
    with open(os.path.join(base, 'recipe/sequenced_assembly/ifmoded/durable_shutter_without.json'), 'r', encoding='utf-8') as f:
        template = json.load(f)
    template['neoforge:conditions'] = conds
    ing = template.get('ingredient', {})
    if is_tag:
        ing['tag'] = ing_ref
        if 'item' in ing:
            del ing['item']
    else:
        ing['item'] = ing_ref
        if 'tag' in ing:
            del ing['tag']
    template['ingredient'] = ing
    return template

def make_ifmoded_deagle(suffix, conds, ing_ref, is_tag):
    with open(os.path.join(base, 'recipe/sequenced_assembly/ifmoded/shutter_deagle_without.json'), 'r', encoding='utf-8') as f:
        template = json.load(f)
    template['neoforge:conditions'] = conds
    ing = template.get('ingredient', {})
    if is_tag:
        ing['tag'] = ing_ref
        if 'item' in ing:
            del ing['item']
    else:
        ing['item'] = ing_ref
        if 'tag' in ing:
            del ing['tag']
    template['ingredient'] = ing
    return template

# Generate rolling
rolling_dir = os.path.join(base, 'recipe/rolling')
for suffix, conds, ing_ref, is_tag in states:
    path = os.path.join(rolling_dir, 'steel_rod_' + suffix + '.json')
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(make_rolling(suffix, conds, ing_ref, is_tag), f, indent=2, ensure_ascii=False)
    print('rolling', suffix)

# Pressing
pressing_dir = os.path.join(base, 'recipe/pressing')
for suffix, conds, ing_ref, is_tag in states:
    path = os.path.join(pressing_dir, 'steel_sheet_' + suffix + '.json')
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(make_pressing(suffix, conds, ing_ref, is_tag), f, indent=2, ensure_ascii=False)
    print('pressing', suffix)

# Mixing (only conditions change, output stays catr steel)
mixing_dir = os.path.join(base, 'recipe/mixing')
for suffix, conds, ing_ref, is_tag in states:
    path = os.path.join(mixing_dir, 'steel_ingot_' + suffix + '.json')
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(make_mixing(suffix, conds, ing_ref, is_tag), f, indent=2, ensure_ascii=False)
    print('mixing', suffix)

# Durable shutter ifmoded
dur_dir = os.path.join(base, 'recipe/sequenced_assembly/ifmoded')
for suffix, conds, ing_ref, is_tag in states:
    path = os.path.join(dur_dir, 'durable_shutter_' + suffix + '.json')
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(make_ifmoded_durable(suffix, conds, ing_ref, is_tag), f, indent=2, ensure_ascii=False)
    print('durable_shutter', suffix)

# Deagle ifmoded
for suffix, conds, ing_ref, is_tag in states:
    path = os.path.join(dur_dir, 'shutter_deagle_' + suffix + '.json')
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(make_ifmoded_deagle(suffix, conds, ing_ref, is_tag), f, indent=2, ensure_ascii=False)
    print('shutter_deagle', suffix)

print('FULL COVERAGE COMPLETE')
