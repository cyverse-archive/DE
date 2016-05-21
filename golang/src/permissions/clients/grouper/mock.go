package grouper

type MockGrouperClient struct {
	groups map[string][]*GroupInfo
}

func NewMockGrouperClient(groups map[string][]*GroupInfo) MockGrouperClient {
	return MockGrouperClient{groups: groups}
}

func (gc *MockGrouperClient) GroupsForSubject(subjectId string) ([]*GroupInfo, error) {
	return gc.groups[subjectId], nil
}
