var webpackConfig = require('./webpack.test');

module.exports = function (config) {
    var _config = {
        autoWatch: false,
        basePath: '',
        browsers: ['PhantomJS'],
        colors: true,
        files: [{pattern: './config/karma-test-shim.js', watched: false}],
        frameworks: ['jasmine', 'source-map-support'],
        logLevel: config.LOG_INFO,
        port: 9876,
        preprocessors: { './config/karma-test-shim.js': ['webpack', 'sourcemap']},
        reporters: ['mocha', 'junit', 'coverage'],
        singleRun: true,
        webpack: webpackConfig,
        webpackMiddleware: { stats: 'errors-only'},
        webpackServer: { noInfo: true},

        junitReporter: {
            outputDir: '../../build/frontendTest-results',
            outputFile: undefined,
            suite: '',
            useBrowserName: true,
            nameFormatter: undefined,
            classNameFormatter: undefined,
            properties: {}
        },

        coverageReporter: {
            reporters: [
                {
                    type: 'json',
                    dir: '../../build/frontendTest-coverage',
                    subdir: '.'
                }
            ]
        }
    };

    config.set(_config);
};

