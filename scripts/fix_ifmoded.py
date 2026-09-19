
import json, os
base = r'D:\dow\mine2_catr\catrmodd\src\main\resources\data\catr'
with open(os.path.join(base, 'recipe/sequenced_assembly/ifmoded/durable_shutter_without.json'), 'r', encoding='utf-8') as f:
    template_dur = json.load(f)
with open(os.path.join(base, 'recipe/sequenced_assembly/ifmoded/shutter_deagle_without.json'), 'r', encoding='utf-8') as f:
    template_deg = json.load(f)

states = [
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

dir_path = os.path.join(base, 'recipe/sequenced_assembly/ifmoded')

for suffix, conds, ing_ref, is_tag in states:
    for template, base_name in [(template_dur, 'durable_shutter'), (template_deg, 'shutter_deagle')]:
        data = json.loads(json.dumps(template))  # deep copy
        data['neoforge:conditions'] = conds
        ing = data.get('ingredient', {})
        if is_tag:
            ing['tag'] = ing_ref
            if 'item' in ing: del ing['item']
        else:
            ing['item'] = ing_ref
            if 'tag' in ing: del ing['tag']
        data['ingredient'] = ing
        out_path = os.path.join(dir_path, base_name + '_' + suffix + '.json')
        with open(out_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        print('ifmoded', base_name, suffix)

print('IFMODED FULL OK')
