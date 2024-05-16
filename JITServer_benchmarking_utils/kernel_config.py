import json


def setup_kernel_args(json_file, jit_server_args):
    config = json.load(open(json_file, 'r'))

    for key in config.keys():
        if key == "kernels":
            jit_server_args.write('BumbleBench.classesToInvoc=')
            for kernel_config in config["kernels"]:
                jit_server_args.write(f'{kernel_config["kernel_name"]} {kernel_config["invoc_count"]} ')

            jit_server_args.write('\n')
        elif key == "multi-threaded":
            jit_server_args.write('BumbleBench.isMultiThreaded=')
            if config[key]:
                jit_server_args.write('true\n')
            else:
                jit_server_args.write('false\n')

    jit_server_args.close()
