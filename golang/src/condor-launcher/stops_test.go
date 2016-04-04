package main

import (
	"bytes"
	"encoding/json"
	"io"
	"messaging"
	"os"
	"reflect"
	"strings"
	"testing"

	"github.com/streadway/amqp"
)

var client *messaging.Client

func shouldrun() bool {
	if os.Getenv("RUN_INTEGRATION_TESTS") != "" {
		return true
	}
	return false
}

func GetClient(t *testing.T) *messaging.Client {
	var err error
	if client != nil {
		return client
	}
	client, err = messaging.NewClient(messagingURI(), false)
	if err != nil {
		t.Error(err)
	}
	client.SetupPublishing(messaging.JobsExchange)
	go client.Listen()
	return client
}

func messagingURI() string {
	return "amqp://guest:guest@rabbit:5672/"
}

var (
	listing = []byte(`RecentBlockReadKbytes = 0
IpcJobId = "generated_script"
BlockWrites = 0
BlockWriteKbytes = 0
BlockReads = 0
BlockReadKbytes = 0
BytesSent = 103469.0
JobFinishedHookDone = 1437604709
NumShadowStarts = 1
JobStatus = 4
JobCurrentStartDate = 1437604662
TerminationPending = true
MachineAttrSlotWeight0 = 3
LastJobLeaseRenewal = 1437604708
LastMatchTime = 1437604662
LastRemoteHost = "slot1@shadowcat.iplantcollaborative.org"
MemoryUsage = ( ( ResidentSetSize + 1023 ) / 1024 )
IpcExe = "wc_wrapper.sh"
IpcUuid = "eca67a7c-e745-4e98-b892-67a9948bc2cb"
NumCkpts = 0
ClusterId = 3
CurrentTime = time()
PeriodicRelease = false
WantCheckpoint = false
Arguments = "iplant.sh"
RemoteWallClockTime = 47.0
ImageSize_RAW = 194760
LeaveJobInQueue = false
BufferBlockSize = 32768
ExitCode = 0
CompletionDate = 1437604709
User = "condor@iplantcollaborative.org"
RemoteSysCpu = 0.0
ExecutableSize = 1000
LocalUserCpu = 0.0
TransferInput = "iplant.sh,irods-config,iplant.cmd"
RemoteUserCpu = 0.0
NiceUser = false
JobRunCount = 1
CumulativeSlotTime = 141.0
TransferInputSizeMB = 0
CondorPlatform = "$CondorPlatform: X86_64-RedHat_7.0 $"
Environment = ""
BufferSize = 524288
Owner = "condor"
JobNotification = 0
BytesRecvd = 963523.0
RequestMemory = ifthenelse(MemoryUsage =!= undefined,MemoryUsage,( ImageSize + 1023 ) / 1024)
MaxHosts = 1
UserLog = "/tmp/sriram/Word_Count_analysis1-2015-07-22-15-37-34.705/logs/condor.log"
IpcUsername = "sriram"
Out = "script-output.log"
MinHosts = 1
Requirements = ( TARGET.Arch == "X86_64" ) && ( TARGET.OpSys == "LINUX" ) && ( TARGET.Memory >= RequestMemory ) && ( TARGET.HasFileTransfer )
SpooledOutputFiles = "de-transfer-trigger.log"
RequestCpus = 1
AutoClusterAttrs = "JobUniverse,LastCheckpointPlatform,NumCkpts,IpcExe,MachineLastMatchTime,ImageSize,MemoryUsage,RequestMemory,ResidentSetSize,Requirements,Rank,NiceUser,ConcurrencyLimits"
JobUniverse = 5
ExitBySignal = false
JobPrio = 0
NumJobMatches = 1
RootDir = "/"
GlobalJobId = "rocinante.iplantcollaborative.org#3.0#1437604654"
CurrentHosts = 0
JobStartDate = 1437604662
CoreSize = 0
OnExitHold = false
LocalSysCpu = 0.0
Iwd = "/tmp/sriram/Word_Count_analysis1-2015-07-22-15-37-34.705/logs"
PeriodicHold = false
ProcId = 0
CommittedSuspensionTime = 0
StatsLifetimeStarter = 45
ImageSize = 200000
CondorVersion = "$CondorVersion: 8.2.8 Apr 07 2015 BuildID: UW_development $"
Err = "script-error.log"
PeriodicRemove = false
ConcurrencyLimits = "sriram"
TransferOutput = "logs/de-transfer-trigger.log"
StreamErr = false
DiskUsage_RAW = 1115
OnExitRemove = true
DiskUsage = 1250
In = "/dev/null"
TargetType = "Machine"
WhenToTransferOutput = "ON_EXIT_OR_EVICT"
ResidentSetSize = 2250
StreamOut = false
WantRemoteIO = true
CommittedSlotTime = 141.0
TotalSuspensions = 0
ExecutableSize_RAW = 938
LastSuspensionTime = 0
CommittedTime = 47
IpcExePath = "/usr/local3/bin/wc_tool-1.00"
Cmd = "/bin/bash"
NumJobStarts = 1
EnteredCurrentStatus = 1437604709
JobLeaseDuration = 1200
QDate = 1437604654
WantRemoteSyscalls = false
MachineAttrCpus0 = 3
ShouldTransferFiles = "YES"
ExitStatus = 0
AutoClusterId = 5
Rank = mips
MyType = "Job"
CumulativeSuspensionTime = 0
NumSystemHolds = 0
NumRestarts = 0
RecentBlockWrites = 0
NumCkpts_RAW = 0
LastPublicClaimId = "<150.135.78.112:63306>#1437583342#12#..."
RecentStatsLifetimeStarter = 37
TransferIn = false
RecentBlockWriteKbytes = 0
JobCurrentStartExecutingDate = 1437604663
LastJobStatus = 2
StartdPrincipal = "unauthenticated@unmapped/150.135.78.112"
ResidentSetSize_RAW = 2188
RequestDisk = 0
OrigMaxHosts = 1
RecentBlockReads = 0

RecentBlockReadKbytes = 0
IpcJobId = "generated_script"
BlockWrites = 0
BlockWriteKbytes = 0
BlockReads = 0
BlockReadKbytes = 0
BytesSent = 106927.0
JobFinishedHookDone = 1437600186
NumShadowStarts = 1
JobStatus = 4
JobCurrentStartDate = 1437599749
TerminationPending = true
MachineAttrSlotWeight0 = 3
LastJobLeaseRenewal = 1437600185
LastMatchTime = 1437599749
LastRemoteHost = "slot1@shadowcat.iplantcollaborative.org"
MemoryUsage = ( ( ResidentSetSize + 1023 ) / 1024 )
IpcExe = "wc_wrapper.sh"
NumCkpts = 0
ClusterId = 1
CurrentTime = time()
PeriodicRelease = false
WantCheckpoint = false
Arguments = "iplant.sh"
RemoteWallClockTime = 437.0
ImageSize_RAW = 196812
LeaveJobInQueue = false
BufferBlockSize = 32768
ExitCode = 0
CompletionDate = 1437600186
User = "condor@iplantcollaborative.org"
RemoteSysCpu = 0.0
ExecutableSize = 1000
LocalUserCpu = 0.0
TransferInput = "iplant.sh,irods-config,iplant.cmd"
RemoteUserCpu = 1.0
NiceUser = false
JobRunCount = 1
CumulativeSlotTime = 1311.0
TransferInputSizeMB = 0
CondorPlatform = "$CondorPlatform: X86_64-RedHat_7.0 $"
Environment = ""
BufferSize = 524288
Owner = "condor"
JobNotification = 0
BytesRecvd = 963569.0
RequestMemory = ifthenelse(MemoryUsage =!= undefined,MemoryUsage,( ImageSize + 1023 ) / 1024)
MaxHosts = 1
UserLog = "/tmp/wregglej/Word_Count_analysis1-2015-07-22-14-15-39.005/logs/condor.log"
IpcUsername = "wregglej"
Out = "script-output.log"
MinHosts = 1
Requirements = ( TARGET.Arch == "X86_64" ) && ( TARGET.OpSys == "LINUX" ) && ( TARGET.Memory >= RequestMemory ) && ( TARGET.HasFileTransfer )
SpooledOutputFiles = "de-transfer-trigger.log"
RequestCpus = 1
JobUniverse = 5
ExitBySignal = false
JobPrio = 0
NumJobMatches = 1
RootDir = "/"
GlobalJobId = "rocinante.iplantcollaborative.org#1.0#1437599739"
CurrentHosts = 0
JobStartDate = 1437599749
CoreSize = 0
OnExitHold = false
LocalSysCpu = 0.0
Iwd = "/tmp/wregglej/Word_Count_analysis1-2015-07-22-14-15-39.005/logs"
PeriodicHold = false
ProcId = 0
CommittedSuspensionTime = 0
StatsLifetimeStarter = 436
ImageSize = 200000
CondorVersion = "$CondorVersion: 8.2.8 Apr 07 2015 BuildID: UW_development $"
Err = "script-error.log"
PeriodicRemove = false
ConcurrencyLimits = "wregglej"
TransferOutput = "logs/de-transfer-trigger.log"
StreamErr = false
DiskUsage_RAW = 1118
OnExitRemove = true
DiskUsage = 1250
In = "/dev/null"
TargetType = "Machine"
WhenToTransferOutput = "ON_EXIT_OR_EVICT"
ResidentSetSize = 10000
StreamOut = false
WantRemoteIO = true
CommittedSlotTime = 1311.0
TotalSuspensions = 0
ExecutableSize_RAW = 938
LastSuspensionTime = 0
CommittedTime = 437
IpcUuid = "b788569f-6948-4586-b5bd-5ea096986331"
NumJobStarts = 1
EnteredCurrentStatus = 1437600186
JobLeaseDuration = 1200
QDate = 1437599739
WantRemoteSyscalls = false
MachineAttrCpus0 = 3
ShouldTransferFiles = "YES"
ExitStatus = 0
Rank = mips
MyType = "Job"
CumulativeSuspensionTime = 0
NumSystemHolds = 0
NumRestarts = 0
RecentBlockWrites = 0
NumCkpts_RAW = 0
LastPublicClaimId = "<150.135.78.112:63306>#1437583342#1#..."
RecentStatsLifetimeStarter = 427
TransferIn = false
RecentBlockWriteKbytes = 0
JobCurrentStartExecutingDate = 1437599749
LastJobStatus = 2
StartdPrincipal = "unauthenticated@unmapped/150.135.78.112"
ResidentSetSize_RAW = 9676
RequestDisk = 0
OrigMaxHosts = 1
RecentBlockReads = 0
Cmd = "/bin/bash"
IpcExePath = "/usr/local3/bin/wc_tool-1.00"

RecentBlockReadKbytes = 0
IpcJobId = "generated_script"
BlockWrites = 0
BlockWriteKbytes = 0
BlockReads = 0
BlockReadKbytes = 0
BytesSent = 99709.0
JobFinishedHookDone = 1437600183
NumShadowStarts = 1
JobStatus = 4
JobCurrentStartDate = 1437599789
TerminationPending = true
MachineAttrSlotWeight0 = 3
LastJobLeaseRenewal = 1437600182
LastMatchTime = 1437599789
LastRemoteHost = "slot2@shadowcat.iplantcollaborative.org"
MemoryUsage = ( ( ResidentSetSize + 1023 ) / 1024 )
IpcExe = "wc_wrapper.sh"
IpcUuid = "63c5523d-d8a5-49bc-addc-99a73566cd89"
NumCkpts = 0
ClusterId = 2
CurrentTime = time()
PeriodicRelease = false
WantCheckpoint = false
Arguments = "iplant.sh"
RemoteWallClockTime = 394.0
ImageSize_RAW = 196812
LeaveJobInQueue = false
BufferBlockSize = 32768
ExitCode = 1
CompletionDate = 1437600183
User = "condor@iplantcollaborative.org"
RemoteSysCpu = 0.0
ExecutableSize = 1000
LocalUserCpu = 0.0
TransferInput = "iplant.sh,irods-config,iplant.cmd"
RemoteUserCpu = 0.0
NiceUser = false
JobRunCount = 1
CumulativeSlotTime = 1182.0
TransferInputSizeMB = 0
CondorPlatform = "$CondorPlatform: X86_64-RedHat_7.0 $"
Environment = ""
BufferSize = 524288
Owner = "condor"
JobNotification = 0
BytesRecvd = 963523.0
RequestMemory = ifthenelse(MemoryUsage =!= undefined,MemoryUsage,( ImageSize + 1023 ) / 1024)
MaxHosts = 1
UserLog = "/tmp/sriram/Word_Count_analysis1-2015-07-22-14-16-29.284/logs/condor.log"
IpcUsername = "sriram"
Out = "script-output.log"
MinHosts = 1
Requirements = ( TARGET.Arch == "X86_64" ) && ( TARGET.OpSys == "LINUX" ) && ( TARGET.Memory >= RequestMemory ) && ( TARGET.HasFileTransfer )
SpooledOutputFiles = "de-transfer-trigger.log"
RequestCpus = 1
AutoClusterAttrs = "JobUniverse,LastCheckpointPlatform,NumCkpts,IpcExe,MachineLastMatchTime,ImageSize,MemoryUsage,RequestMemory,ResidentSetSize,Requirements,Rank,NiceUser,ConcurrencyLimits"
JobUniverse = 5
ExitBySignal = false
JobPrio = 0
NumJobMatches = 1
RootDir = "/"
GlobalJobId = "rocinante.iplantcollaborative.org#2.0#1437599789"
CurrentHosts = 0
JobStartDate = 1437599789
CoreSize = 0
OnExitHold = false
LocalSysCpu = 0.0
Iwd = "/tmp/sriram/Word_Count_analysis1-2015-07-22-14-16-29.284/logs"
PeriodicHold = false
ProcId = 0
CommittedSuspensionTime = 0
StatsLifetimeStarter = 393
ImageSize = 200000
CondorVersion = "$CondorVersion: 8.2.8 Apr 07 2015 BuildID: UW_development $"
Err = "script-error.log"
PeriodicRemove = false
ConcurrencyLimits = "sriram"
TransferOutput = "logs/de-transfer-trigger.log"
StreamErr = false
DiskUsage_RAW = 1107
OnExitRemove = true
DiskUsage = 1250
In = "/dev/null"
TargetType = "Machine"
WhenToTransferOutput = "ON_EXIT_OR_EVICT"
ResidentSetSize = 10000
StreamOut = false
WantRemoteIO = true
CommittedSlotTime = 1182.0
TotalSuspensions = 0
ExecutableSize_RAW = 938
LastSuspensionTime = 0
CommittedTime = 394
IpcExePath = "/usr/local3/bin/wc_tool-1.00"
Cmd = "/bin/bash"
NumJobStarts = 1
EnteredCurrentStatus = 1437600183
JobLeaseDuration = 1200
QDate = 1437599789
WantRemoteSyscalls = false
MachineAttrCpus0 = 3
ShouldTransferFiles = "YES"
ExitStatus = 0
AutoClusterId = 3
Rank = mips
MyType = "Job"
CumulativeSuspensionTime = 0
NumSystemHolds = 0
NumRestarts = 0
RecentBlockWrites = 0
NumCkpts_RAW = 0
LastPublicClaimId = "<150.135.78.112:63306>#1437583342#2#..."
RecentStatsLifetimeStarter = 384
TransferIn = false
RecentBlockWriteKbytes = 0
JobCurrentStartExecutingDate = 1437599789
LastJobStatus = 2
StartdPrincipal = "unauthenticated@unmapped/150.135.78.112"
ResidentSetSize_RAW = 9488
RequestDisk = 0
OrigMaxHosts = 1
RecentBlockReads = 0`)
)

func TestCondorID(t *testing.T) {
	expected := "2"
	invID := "63c5523d-d8a5-49bc-addc-99a73566cd89"
	actual := queueEntriesByInvocationID(listing, invID)
	found := false
	for _, entry := range actual {
		if entry.CondorID == expected {
			found = true
		}
	}
	if !found {
		t.Errorf("The expected CondorID of %s was not found", expected)
	}

	invID = "b788569f-6948-4586-b5bd-5ea096986331"
	expected = "1"
	actual = queueEntriesByInvocationID(listing, invID)
	found = false
	for _, entry := range actual {
		if entry.CondorID == expected {
			found = true
		}
	}
	if !found {
		t.Errorf("The expected CondorID of %s was not found", expected)
	}

	invID = "eca67a7c-e745-4e98-b892-67a9948bc2cb"
	expected = "3"
	actual = queueEntriesByInvocationID(listing, invID)
	found = false
	for _, entry := range actual {
		if entry.CondorID == expected {
			found = true
		}
	}
	if !found {
		t.Errorf("The expected CondorID of %s was not found", expected)
	}
}

func TestExecCondorQ(t *testing.T) {
	inittests(t)
	output, err := ExecCondorQ()
	if err != nil {
		t.Error(err)
	}
	invID := "63c5523d-d8a5-49bc-addc-99a73566cd89"
	actual := queueEntriesByInvocationID(output, invID)
	expected := "2"
	found := false
	for _, entries := range actual {
		if entries.CondorID == expected {
			found = true
		}
	}
	if !found {
		t.Errorf("The expected CondorID of %s was not found", expected)
	}

	invID = "b788569f-6948-4586-b5bd-5ea096986331"
	expected = "1"
	actual = queueEntriesByInvocationID(output, invID)
	found = false
	for _, entry := range actual {
		if entry.CondorID == expected {
			found = true
		}
	}
	if !found {
		t.Errorf("The expected CondorID of %s was not found", expected)
	}

	invID = "eca67a7c-e745-4e98-b892-67a9948bc2cb"
	expected = "3"
	actual = queueEntriesByInvocationID(output, invID)
	found = false
	for _, entry := range actual {
		if entry.CondorID == expected {
			found = true
		}
	}
	if !found {
		t.Errorf("The expected CondorID of %s was not found", expected)
	}
}

func TestExecCondorRm(t *testing.T) {
	inittests(t)
	actual, err := ExecCondorRm("foo")
	if err != nil {
		t.Error(err)
	}
	expected := []byte("CondorID foo was stopped\n")
	if !reflect.DeepEqual(actual, expected) {
		t.Errorf("ExecCondorRm returned '%s' instead of '%s'", actual, expected)
	}
}

func TestStopHandler(t *testing.T) {
	var (
		coord      chan string
		err        error
		marshalled []byte
	)
	if !shouldrun() {
		return
	}
	inittests(t)
	stopMsg := messaging.StopRequest{
		InvocationID: "b788569f-6948-4586-b5bd-5ea096986331",
	}
	if marshalled, err = json.Marshal(stopMsg); err != nil {
		t.Error(err)
	}
	msg := amqp.Delivery{
		Body: marshalled,
	}
	old := os.Stdout
	defer func() {
		os.Stdout = old
	}()
	r, w, err := os.Pipe()
	if err != nil {
		t.Error(err)
	}
	os.Stdout = w
	coord = make(chan string)
	go func() {
		var buf bytes.Buffer
		io.Copy(&buf, r)
		coord <- buf.String()
	}()
	client := GetClient(t)
	stopHandler(client)(msg)
	w.Close()
	actual := <-coord
	if !strings.Contains(actual, "Running condor_q...") {
		t.Error("Logging output from stopHandler does not contain 'Running condor_q...'")
	}
	if !strings.Contains(actual, "Done running condor_q") {
		t.Error("Logging output from stopHandler does not contain 'Done running condor_q'")
	}
	if !strings.Contains(actual, "Running 'condor_rm 1'") {
		t.Error("Logging output from stopHandler does not contain \"Running 'condor_rm 1'\"")
	}
	if !strings.Contains(actual, "Output of 'condor_rm 1'") {
		t.Error("Logging output from stopHandler does not contain \"Output of 'condor_rm 1'\"")
	}
}
