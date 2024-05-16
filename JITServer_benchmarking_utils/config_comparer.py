import json
import argparse
from pathlib import Path
import shutil
import hashlib

def create_unique_hash(json_dict) -> str:
    dump = json.dumps(json_dict, sort_keys=True)
    val = hashlib.sha1(dump.encode("utf-8")).hexdigest()
    return val

def compare_json_hashes(json_file, compare_file) -> bool:
    json_file_dict = json.load(open(json_file, 'r'))
    compare_file_dict = json.load(open(json_file, 'r'))
    if create_unique_hash(json_file_dict) == create_unique_hash(compare_file_dict):
        return True
    else:
        return False
if __name__ == "__main__":
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
        if compare_json_hashes(json_file, str(file)):
            print(json_file + " is identical to " + str(file))
            if replace:
                print("replaced contents of " + json_file + " with " + str(file))
                shutil.copy(file.relative_to("."), json_file)

