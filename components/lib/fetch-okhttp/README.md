# [Android Components](../../../README.md) > Concept > Fetch

The `concept-fetch` component contains interfaces for defining an abstract HTTP client for fetching resources.

The primary use of this component is to hide the actual implementation of the HTTP client from components required to make HTTP requests. This allows apps to configure a single app-wide used client without the components enforcing a particular dependency.

The API and name of the component is inspired by the [Web Fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API).

## Usage

### Setting up the dependency

Use Gradle to download the library from [maven.mozilla.org](https://maven.mozilla.org/) ([Setup repository](../../../README.md#maven-repository)):

```Groovy
implementation "org.mozilla.components:concept-fetch:{latest-version}"
```

## License

    This Source Code Form is subject to the terms of the Mozilla Public
    License, v. 2.0. If a copy of the MPL was not distributed with this
    file, You can obtain one at http://mozilla.org/MPL/2.0/
