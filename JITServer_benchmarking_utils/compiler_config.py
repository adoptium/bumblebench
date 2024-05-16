import json


def get_compiler_args(json_file, log_directory):
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
    return xjit_flags, xaot_flags, other_flags
