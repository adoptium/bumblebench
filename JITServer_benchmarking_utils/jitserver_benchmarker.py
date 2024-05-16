import os
import json
import argparse
import hashlib
from pathlib import Path
import shutil
from datetime import datetime

def generate_directory(file_path)-> str:
    BUF_SIZE = 65536
    sha1 = hashlib.sha1()
    with open(file_path, 'rb') as f:
        while True:
            data = f.read(BUF_SIZE)
            if not data:
                break
            sha1.update(data)

    return sha1.hexdigest()

parser = argparse.ArgumentParser(
    prog='jitserver_benchmarker',
    description="A Script that takes in a configuration of kernels and uses BumbleBench for JITServer to benchmark"
)

parser.add_argument('-o', '--openj9_path', required=True)
parser.add_argument('-c', '--configuration', required=True)
parser.add_argument('-b', '--bumblebench_jitserver_path', required=True)
parser.add_argument('-l', '--loud_output', action='store_true')

args = vars(parser.parse_args())

json_file = args['configuration']
openj9_path = args['openj9_path']
bumblebench_jitserver_path = args['bumblebench_jitserver_path']
loud_output = args['loud_output']

jit_server_args = open('./JITServerArgs.txt', 'w')

log_directory = generate_directory(json_file)

Path(log_directory).mkdir(parents=True, exist_ok=True)
config = json.load(open(json_file, 'r'))
xjit_flags = '-Xjit:'
xaot_flags = '-Xaot'
other_flags = ''
for key in config.keys():
    if key.startswith("-Xjit"):
        strings = key.split(":")
        if strings[1] == "count":
            xjit_flags += "count" + '=' + str(config[key]) + ","
        if strings[1] == "compiler_method_options":
            for kernel_conf in config[key]:
                xjit_flags += "'{" + kernel_conf["method_signature"] + "}(" + kernel_conf["options"] + ")',"
        if strings[1] == "verbose,vlog":
            xjit_flags += "verbose,vlog" + '=' + log_directory + "/" + str(config[key]) + ","
    elif key == "kernels":
        jit_server_args.write('BumbleBench.classesToInvoc=')
        for kernel_conf in config['kernels']:
            jit_server_args.write(f'{kernel_conf["kernel_name"]} {kernel_conf["invoc_count"]}')
    elif key.startswith("-Xaot"):
        strings = key.split(":")
        if strings[1] == "count":
            xaot_flags += ":count" + '=' + str(config[key]) + ","
    elif key == "-Xnoaot":
        if not config[key]:
            other_flags += key + " "
    elif key == "-XX:UseJITServer":
        sign = "+" if config[key] else "-"
        other_flags += "-XX:" + sign + "UseJITServer"

jit_server_args.close()

now = str(datetime.now())
now = now.replace(" ", ".").replace(":", "").replace("-","")
shutil.copy(json_file, log_directory)
if loud_output:
    print(
        f'{openj9_path} -jar {xjit_flags} {xaot_flags} {other_flags} {bumblebench_jitserver_path}/BumbleBench.jar JITserver')
    os.system(
        f'{openj9_path} -jar {xjit_flags} {xaot_flags} {other_flags} {bumblebench_jitserver_path}/BumbleBench.jar JITserver')
else:
    print(
        f'{openj9_path} -jar {xjit_flags} {xaot_flags} {other_flags} {bumblebench_jitserver_path}/BumbleBench.jar JITserver > {log_directory}/output_file.{now}')
    os.system(
        f'{openj9_path} -jar {xjit_flags} {xaot_flags} {other_flags} {bumblebench_jitserver_path}/BumbleBench.jar JITserver > {log_directory}/output_file.{now}')
