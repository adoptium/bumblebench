import json
import argparse
from pathlib import Path
import shutil

def compare_json(json_file, compare_file) -> bool:
    json_config = json.load(open(json_file, 'r'))
    compare_config = json.load(open(compare_file, 'r'))
    if not recursive_compare(json_config, compare_config):
        return False
    if not recursive_compare(compare_config, json_config):
        return False

    return True
    
def recursive_compare(json_config, compare_config) -> bool:
    if isinstance(json_config,dict) and isinstance(compare_config,dict):
        for key in json_config.keys():
            if key in compare_config.keys():
                if not recursive_compare(json_config[key], compare_config[key]):
                    return False
            else:
                return False
    elif isinstance(json_config,list) and isinstance(compare_config,list):
        for i in json_config:
            one_match = False
            for j in compare_config:
                if recursive_compare(i,j):
                    one_match = True
            if not one_match:
                return False
    elif not isinstance(json_config,dict) and not isinstance(compare_config,dict) and not isinstance(json_config,list) and not isinstance(compare_config,list):
        if json_config != compare_config:
            return False
    else:
        return False
    return True

    
parser = argparse.ArgumentParser(
    prog='config_comparer',
    description="A Script that takes in a json file and checks if it already exists in this directory (and subdirectories)"
)
parser.add_argument('-j', '--json_file_path', required=True)
parser.add_argument('-r', '--replace', action='store_true')
args = vars(parser.parse_args())
json_file = args['json_file_path']
replace = args['replace']
paths = list(Path('.').glob('**/*.json'))

index_marked = -1
for i in range(len(paths)):
    if paths[i].name == json_file:
        index_marked = i
        break
paths.pop(index_marked)

for file in paths:
    if compare_json(json_file, str(file)):
        print(json_file + " is identical to " + str(file))
        if replace:
            print("replaced contents of " + json_file + " with " + str(file))
            shutil.copy(file.relative_to("."), json_file)

