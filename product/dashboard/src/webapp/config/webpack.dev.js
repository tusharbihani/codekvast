process.env.CODEKVAST_VERSION = 'dev';
process.env.ENV = 'development';

var webpackMerge = require('webpack-merge');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var commonConfig = require('./webpack.common.js');
var helpers = require('./helpers');

module.exports = webpackMerge(commonConfig, {
    devtool: 'cheap-module-eval-source-map',

    output: {
        path: helpers.root('dist'),
        publicPath: '/',
        filename: '[name].js',
        chunkFilename: '[id].chunk.js'
    },

    plugins: [
        new ExtractTextPlugin('[name].css')
    ],

    devServer: {
        inline: true,
        port: 8089,
        historyApiFallback: true,
        stats: 'minimal',
        proxy: {
            '/api-docs': 'http://localhost:8081',
            '/swagger-ui.html': 'http://localhost:8081',
            '/webapp': 'http://localhost:8081'
        }
    }
});
