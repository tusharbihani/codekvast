/**
 * The state for MethodsComponent.
 */

const oneHourInMillis = 60 * 60 * 1000;
const oneDayInMillis = 24 * oneHourInMillis;

export class Settings {
    static KEY = 'settings';
    readonly sampleTimestamp = new Date().getTime() - 37 * oneDayInMillis - 13 * oneHourInMillis;

    dateFormat = 'short';

    constructor() {
        console.log('Created Settings')
    }

    dateFormatHeader() {
        switch(this.dateFormat) {
            case 'age': return 'Age';
            default: return 'Date';
        }
    }
}

