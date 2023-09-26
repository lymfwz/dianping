-- key : 存放 uu + threadId的key
-- value : uu + threadId
-- local id = redis.call("get", key)
if(redis.call('get', KEYS[1]) == ARGV[1]) then
    redis.call("del", KEYS[1])
end
return 0