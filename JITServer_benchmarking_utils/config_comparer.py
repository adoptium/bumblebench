import json
import argparse
from pathlib import Path
import shutil
import hashlib


def hunt_for_copies(json_file, replace) -> None:
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


def create_unique_hash(json_dict) -> str:
    # TODO: NOTE THAT THIS PRESERVES ORDERS OF LISTS WHEN HASHING
    dump = json.dumps(json_dict, sort_keys=True)
    return create_hash_from_str(dump)

def create_hash_from_str(string) -> str:
    return hashlib.sha1(string.encode("utf-8")).hexdigest()


def create_unique_hash_from_path(json_file, hunt) -> str:
    if hunt:
        hunt_for_copies(json_file, True)
    config = json.load(open(json_file, 'r'))
    return create_unique_hash(config)


def compare_json_hashes(json_file, compare_file) -> bool:
    json_file_dict = json.load(open(json_file, 'r'))
    compare_file_dict = json.load(open(compare_file, 'r'))
    if create_unique_hash(json_file_dict) == create_unique_hash(compare_file_dict):
        return True
    else:
        return False


def recursive_compare(json_config, compare_config) -> bool:
    if isinstance(json_config, dict) and isinstance(compare_config, dict):
        for key in json_config.keys():
            if key in compare_config.keys():
                if not recursive_compare(json_config[key], compare_config[key]):
                    return False
            else:
                return False
    elif isinstance(json_config, list) and isinstance(compare_config, list):
        for i in json_config:
            one_match = False
            for j in compare_config:
                if recursive_compare(i, j):
                    one_match = True
            if not one_match:
                return False
    elif not isinstance(json_config, dict) and not isinstance(compare_config, dict) and not isinstance(json_config,
                                                                                                       list) and not isinstance(
            compare_config, list):
        if json_config != compare_config:
            return False
    else:
        return False
    return True


def compare_json(json_file, compare_file) -> bool:
    json_config = json.load(open(json_file, 'r'))
    compare_config = json.load(open(compare_file, 'r'))
    if not recursive_compare(json_config, compare_config):
        return False
    if not recursive_compare(compare_config, json_config):
        return False

    return True


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
    hunt_for_copies(json_file, replace)
