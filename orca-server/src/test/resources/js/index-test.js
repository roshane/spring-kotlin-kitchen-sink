const serialize = (input) => JSON.stringify(input);

const deserialize = (json) => JSON.parse(json);

const sayHello = (input, context) => {
  console.log("executing sayHello...");
  try {
    const _input = deserialize(input);
    const _context = deserialize(context);
    console.log("input", _input);
    console.log("context", _context);
    return serialize({
      message: "hello",
    });
  } catch (error) {
    console.log("Error script", error);
  }
};

const useInputAndContextArgs = (input, context) => {
  console.log("input", input, "context", context);
  const _input = deserialize(input);
  const { numbers } = deserialize(context);
  const result = [];
  numbers.forEach((n) => result.push(n));
  result.push(_input);
  return serialize(result);
};

const groupUsersByAge = (input, context) => {
  const _input = deserialize(input);
  return serialize(groupBy(_input, "age"));
};

const groupPostsByUser = (input, context) => {
  const posts = deserialize(input);
  const { users } = deserialize(context);
  const result = {};
  const postsByUserId = groupBy(posts, "userId");
  users.forEach((user) => {
    result[user.id] = {
      ...user,
      ...{ posts: postsByUserId[user.id] },
    };
  });
  return serialize(result);
};


const __main__ = (_input, _context, callback) => {
  try {
    const input = deserialize(input);
    const context = deserialize(context);
    return callback(input, context);
  } catch (error) {
    console.log(error);
    throw error;
  }
};

const groupBy = (dataList, key) => {
  const result = {};
  dataList.forEach((data) => {
    if (result[data[key]]) {
      result[data[key]].push(data);
    } else {
      result[data[key]] = [data];
    }
  });
  return result;
};
