import os
import json
from compiler_config import get_compiler_args
from kernel_config import setup_kernel_args
import argparse
from pathlib import Path
import shutil
from datetime import datetime
import config_comparer


parser = argparse.ArgumentParser(
    prog='jitserver_benchmarker',
    description="A Script that takes in a configuration of kernels and uses BumbleBench for JITServer to benchmark"
)

parser.add_argument('-o', '--openj9_path', required=True)
parser.add_argument('-c', '--compiler_configuration', required=True)
parser.add_argument('-b', '--bumblebench_jitserver_path', required=True)
parser.add_argument('-l', '--loud_output', action='store_true')
parser.add_argument('-k', '--kernel_configuration', required=True)

args = vars(parser.parse_args())

compiler_json_file = args['compiler_configuration']
kernel_json_file = args['kernel_configuration']
openj9_path = args['openj9_path']
bumblebench_jitserver_path = args['bumblebench_jitserver_path']
loud_output = args['loud_output']

jit_server_args = open('./JITServerArgs.txt', 'w')
log_directory, xjit_flags, xaot_flags, other_flags = get_compiler_args(compiler_json_file)
setup_kernel_args(kernel_json_file, jit_server_args)

now = str(datetime.now())
now = now.replace(" ", ".").replace(":", "").replace("-","")
shutil.copy(compiler_json_file, log_directory + "/compiler_config.json")

shutil.copy(kernel_json_file, log_directory + "/kernel_config.json")

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
