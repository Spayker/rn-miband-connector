module.exports = {
	preset: "react-native",
	transform: {
		"^.+\\.jsx?$": "<rootDir>/node_modules/babel-jest"
	},
	testRegex: "(/__tests__/.*|(\\.|/)(test|spec))\\.(jsx?|tsx?)$",
	moduleFileExtensions: ["js", "jsx"],
	globals: {
		"ts-jest": {
			useBabelrc: true
		}
	}
};