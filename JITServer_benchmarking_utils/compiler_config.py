import json


def get_compiler_args(json_file, log_directory):
    config = json.load(open(json_file, 'r'))

    xjit_flags = '-Xjit:'
    xaot_flags = '-Xaot:'
    other_flags = ''
    for key in config.keys():
        if key.startswith("JIT"):
            strings = key.split(":")
            strings[1] = strings[1].strip()
            if strings[1] == "global_invocation_count_till_compiled":
                xjit_flags += "count=" + str(config[key]) + ","
            if strings[1] == "method_configs":
                for kernel_conf in config[key]:
                    xjit_flags += "'{" + kernel_conf["method_signature"] + "}("
                    if "temperature" in kernel_conf.keys():
                        xjit_flags += "optLevel=" + kernel_conf["temperature"] + ","
                    if "invocation_count_till_compiled" in kernel_conf.keys():
                        xjit_flags += "count=" + str(kernel_conf["invocation_count_till_compiled"])
                    xjit_flags += ")',"
            if strings[1] == "log_file":
                xjit_flags += "verbose,vlog" + '=' + log_directory + "/" + str(config[key]) + ","
            if strings[1] == "enable_JIT":
                if not config[key]:
                    other_flags += "-Xnojit "
        elif key.startswith("AOT"):
            strings = key.split(":")
            strings[1] = strings[1].strip()
            if strings[1] == "enable_AOT":
                if not config[key]:
                    xaot_flags = "-Xnoaot"
            if xaot_flags != "-Xnoaot":
                if strings[1] == "AOT_count":
                    xaot_flags += "count" + '=' + str(config[key]) + ","
        elif key.startswith("VM"):
            strings = key.split(":")
            strings[1] = strings[1].strip()
            if strings[1] == "use_JIT_server":
                if config[key]:
                    other_flags += "-XX:+UseJITServer"
    return xjit_flags, xaot_flags, other_flags
