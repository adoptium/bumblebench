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
    elif not isinstance(json_config,dict) and not isinstance(compare_config,dict):
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
args = vars(parser.parse_args())
json_file = args['json_file_path']
paths = list(Path('.').glob('**/*.json'))

index_marked = -1
for i in range(len(paths)):
    if paths[i].name == json_file:
        index_marked = i
        break
paths.pop(index_marked)

for file in paths:
    if compare_json(json_file, str(file)):
        #shutil.copy(file.relative_to("."), json_file)
        print(json_file + " is identical to " + str(file))

