module.exports = { 
  entry: {
    gettingStarted: "./gettingStarted.jsx",
    p2: "./p2.jsx",
    p3: "./p3.jsx",
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        use: ["babel-loader"],
      },
      {
        test: /\.css$/,
        use: ["style-loader", "css-loader"],
      },
      {
        test: /\.(png|jpg|gif)$/,
        use: [
          {
            loader: "url-loader",
            options: {
              limit: 8192,
            },
          },
        ],
      },
    ],
  },
  resolve: {
    extensions: [".*", ".js", ".jsx", '.ts', '.tsx'],
  },
  output: {
    path: `${__dirname}/compiled`,
    publicPath: "/",
    filename: "[name].bundle.js",
  },
  mode: "development",
};
