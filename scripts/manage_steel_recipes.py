#!/usr/bin/env python3
"""manage_steel_recipes.py"""
import json, os
PROJECT = r'D:\dow\mine2_catr\catrmodd'
SRC_RES = os.path.join(PROJECT, 'src', 'main', 'resources', 'data', 'catr')
STEEL_MAP = {
    'catr': 'catr:steel_ingot',
    'createnuclear': 'createnuclear:steel_ingot',
    'createbigcannons': 'createbigcannons:steel_ingot',
    'mekanism': 'mekanism:ingot_steel',
}

def build_conditions(present, absent):
    conds = []
    for a in absent:
        conds.append({"type": "neoforge:not", "value": {"type": "neoforge:mod_loaded", "modid": a}})
    for p in present:
        conds.append({"type": "neoforge:mod_loaded", "modid": p})
    return conds

def substitute_steel(obj, steel_id, old_ids):
    if isinstance(obj, dict):
        res = {}
        for k, v in obj.items():
            if k == 'item' and isinstance(v, str) and v in old_ids:
                res[k] = steel_id
            elif k == 'tag' and isinstance(v, str) and v == 'catr:moded_steel':
                res['item'] = steel_id
            else:
                res[k] = substitute_steel(v, steel_id, old_ids)
        return res
    elif isinstance(obj, list):
        return [substitute_steel(i, steel_id, old_ids) for i in obj]
    else:
        return obj

def generate():
    old_ids = set(STEEL_MAP.values())
    new_id = STEEL_MAP['mekanism']
    categories = [
        ('recipe/rolling', 'steel_rod'),
        ('recipe/pressing', 'steel_sheet'),
        ('recipe/mixing', 'steel_ingot'),
        ('recipe/sequenced_assembly/ifmoded', 'durable_shutter'),
        ('recipe/sequenced_assembly/ifmoded', 'shutter_deagle'),
    ]
    for category, base_name in categories:
        src_dir = os.path.join(SRC_RES, category)
        nuclear_file = os.path.join(src_dir, base_name + '_nuclear.json')
        if not os.path.isfile(nuclear_file):
            nuclear_file = os.path.join(src_dir, base_name + '_bigcannons.json')
        if not os.path.isfile(nuclear_file):
            nuclear_file = os.path.join(src_dir, base_name + '_without.json')
        if not os.path.isfile(nuclear_file):
            print('SKIP', category, base_name)
            continue
        with open(nuclear_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        data['neoforge:conditions'] = build_conditions(present=['mekanism'], absent=['createbigcannons', 'createnuclear'])
        data = substitute_steel(data, new_id, old_ids)
        out_file = os.path.join(src_dir, base_name + '_mekanism.json')
        with open(out_file, 'w', encoding='utf-8') as f:
            json.dump(data, f, ensure_ascii=False, indent=2)
        print('GENERATED', os.path.relpath(out_file, SRC_RES))

if __name__ == '__main__':
    generate()
    print('Done.')
