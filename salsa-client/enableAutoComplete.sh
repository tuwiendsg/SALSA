_foo() 
{
    local cur prev opts
    COMPREPLY=()
    cur="${COMP_WORDS[COMP_CWORD]}"
    prev="${COMP_WORDS[COMP_CWORD-1]}"
    #opts="meta syn service-submit service-remove service-status service-list unit-deploy instance-status instance-remove instance-list instance-query"
    opts=`java -jar salsa-client.jar list-commands`
    case "${prev}" in
	service-submit)
	    _filedir
            return 0
            ;;
        *)
        ;;
    esac



    COMPREPLY=( $(compgen -W "${opts}" -- ${cur}) )
    return 0
}
complete -F _foo salsa-client
complete -F _foo salsa-client.jar
complete -F _foo ./salsa-client
