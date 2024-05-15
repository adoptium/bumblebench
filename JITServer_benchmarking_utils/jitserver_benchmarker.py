import os
import json
import argparse

parser = argparse.ArgumentParser(
    prog='jitserver_benchmarker',
    description="A Script that takes in a configuration of kernels and uses BumbleBench for JITServer to benchmark"
)

parser.add_argument('-o', '--openj9_path', required=True)
parser.add_argument('-c', '--configuration', required=True)
parser.add_argument('-b', '--bumblebench_jitserver_path', required=True)

args = vars(parser.parse_args())

json_file = args['configuration']
openj9_path = args['openj9_path']
bumblebench_jitserver_path = args['bumblebench_jitserver_path']

jit_server_args = open('./JITServerArgs.txt', 'w')

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
             xjit_flags += "verbose,vlog" + '=' + str(config[key]) + ","
    elif key == "kernels":
        jit_server_args.write('BumbleBench.classesToInvoc=')
        for kernel_conf in config['kernels']:
            jit_server_args.write(f'{kernel_conf["kernel_name"]} {kernel_conf["invoc_count"]}')
    elif key.startswith("-Xaot"):
        strings = key.split(":")
        if strings[1] == "count":
             xaot_flags += "count" + '=' + str(config[key]) + ","
    elif key == "-Xnoaot"        :
        if config[key] == False:
            other_flags += key + " "

jit_server_args.close()
print(f'{openj9_path} -jar {xjit_flags} {xaot_flags} {other_flags} {bumblebench_jitserver_path}/BumbleBench.jar JITserver')
os.system(f'{openj9_path} -jar {xjit_flags} {xaot_flags} {other_flags} {bumblebench_jitserver_path}/BumbleBench.jar JITserver')
